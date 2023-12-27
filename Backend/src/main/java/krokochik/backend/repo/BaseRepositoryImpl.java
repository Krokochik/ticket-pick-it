package krokochik.backend.repo;

import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import me.tongfei.progressbar.*;
import org.openjdk.jol.info.ClassLayout;
import org.springframework.boot.ApplicationArguments;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Log4j2
@EnableAsync
public abstract class BaseRepositoryImpl<T> implements BaseRepository {
    protected final String storagePath;
    @Getter
    protected T data;

    protected final ApplicationArguments args;
    protected final Gson gson;
    protected boolean async;

    protected BaseRepositoryImpl(Gson gson,
                                 ApplicationArguments args,
                                 T defaultData) {
        this.args = args;
        this.gson = gson;
        this.data = defaultData;

        async = (args.containsOption("async") &&
                !args.getOptionValues("async").contains("off")) ||
                args.getNonOptionArgs().contains("-async");

        if (args.getOptionValues("storage") != null)
            storagePath = args.getOptionValues("storage").get(0);
        else storagePath = System.getProperty("java.io.tmpdir");
    }

    protected BaseRepositoryImpl(Gson gson,
                                 ApplicationArguments args) {
        this(gson, args, null);
    }

    protected abstract String getName();

    protected abstract Type getDataType();

    @Override
    @SneakyThrows
    @PostConstruct
    public @Async void init() {
        val renew = args.getOptionValues("renew");
        if (args.getNonOptionArgs().contains("-renew") ||
                (renew != null && renew.contains(getName()))) {
            saveDataToFile();
        } else loadDataFromFile();
    }

    record Unit(String name, long size) {
    }

    Unit defineUnit(long bytes) {
        Unit unit;
        if (bytes >= 1048576) unit = new Unit(" MiB", 1048576);
        else if (bytes >= 1024) unit = new Unit(" KiB", 1024);
        else unit = new Unit(" bytes", 1);
        return unit;
    }

    @Async
    public Future<Boolean> saveDataToFile() {
        log.info("Start exporting data");
        File file = new File(storagePath
                .replaceAll("\\\\+", "/") + "/" + getName() + ".dat");
        try {
            file.delete();
            file.createNewFile();
        } catch (IOException e) {
            log.error("Something went wrong during writing data to " + file.getAbsolutePath(), e);
            return CompletableFuture.completedFuture(false);
        }

        log.info("Serializing data");
        if (async) {
            String serialized = gson.toJson(data);

            Unit unit = defineUnit(serialized.getBytes().length);
            ProgressBarBuilder pbb = new ProgressBarBuilder()
                    .setTaskName("Writing")
                    .setUnit(unit.name, unit.size)
                    .setStyle(ProgressBarStyle.ASCII)
                    .setUpdateIntervalMillis(100)
                    .setInitialMax(serialized.getBytes().length);

            try (RandomAccessFile raf = new RandomAccessFile(file, "rw");
                 FileChannel channel = raf.getChannel()) {
                ProgressBar pb = pbb.build();

                MappedByteBuffer buffer = channel.map(
                        FileChannel.MapMode.READ_WRITE, 0, serialized.getBytes().length);
                val bytes = serialized.getBytes();
                for (byte aByte : bytes) {
                    buffer.put(aByte);
                    pb.step();
                }
                buffer.force();
                pb.close();
            } catch (IOException e) {
                log.error("Something went wrong during writing data to " + file.getAbsolutePath(), e);
                return CompletableFuture.completedFuture(false);
            }
        } else {
            try {
                ProgressBarBuilder pbb = new ProgressBarBuilder()
                        .setTaskName("Writing")
                        .setStyle(ProgressBarStyle.ASCII)
                        .setUpdateIntervalMillis(100);

                gson.toJson(data, ProgressBar.wrap(new FileWriter(file), pbb));
            } catch (IOException e) {
                log.error("Something went wrong during writing data to " + file.getAbsolutePath(), e);
                return CompletableFuture.completedFuture(false);
            }
        }
        log.info("Data is successfully written to " + file.getAbsolutePath());
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public Future<Boolean> loadDataFromFile() {
        File file = new File(storagePath
                .replaceAll("\\\\+", "/") + "/" + getName() + ".dat");
        log.info("Importing data from " + file.getName());

        Unit unit = defineUnit(file.length());
        ProgressBarBuilder pbb = new ProgressBarBuilder()
                .setTaskName("Reading")
                .setUnit(unit.name, unit.size)
                .setStyle(ProgressBarStyle.ASCII)
                .setUpdateIntervalMillis(100);

        try (FileInputStream fis = new FileInputStream(file)) {
            StringBuilder content = new StringBuilder();

            if (async) {
                FileChannel channel = fis.getChannel();
                ProgressBar pb = pbb.setInitialMax(channel.size()).build();

                MappedByteBuffer buffer = channel.map(
                        FileChannel.MapMode.READ_ONLY, 0, channel.size());
                while (buffer.hasRemaining()) {
                    content.append((char) buffer.get());
                    pb.step();
                }

                pb.close();
                channel.close();
            } else {
                pbb.setInitialMax(file.length());
                Reader reader = ProgressBar.wrap(new FileReader(file), pbb);
                int nextChar;
                while ((nextChar = reader.read()) != -1) {
                    content.append((char) nextChar);
                }
                reader.close();
            }

            log.info("Deserializing data");
            val data = gson.fromJson(content.toString(), getDataType());
            if (data != null) {
                this.data = (T) data;
            }

            log.info("Data is imported from " + file.getAbsolutePath());
            return CompletableFuture.completedFuture(true);
        } catch (FileNotFoundException e) {
            log.warn("Something went wrong during import data from " +
                    file.getAbsolutePath() + " : File not found");
            return CompletableFuture.completedFuture(false);
        } catch (Exception e) {
            log.error("Something went wrong during import data from " +
                    file.getAbsolutePath(), e);
            return CompletableFuture.completedFuture(false);
        }
    }
}
