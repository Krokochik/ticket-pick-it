package krokochik.backend.config;

import krokochik.backend.repo.BaseRepository;
import krokochik.backend.repo.ClinicRepository;
import krokochik.backend.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.nio.file.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Configuration
@EnableAsync
@EnableCaching
public class RunConfigurer {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    ClinicRepository clinicRepository;

    @Autowired
    UserRepository userRepository;

    @Bean
    public ApplicationRunner runner() {
        return args -> {
            var temp = Paths.get(System.getProperty("java.io.tmpdir"));
            if (args.getOptionValues("storage") != null)
                temp = Paths.get(args.getOptionValues("storage").get(0));

            Path path = temp;
            CompletableFuture.runAsync(() -> {
                try (WatchService watchService =
                             FileSystems.getDefault().newWatchService()) {

                    path.register(watchService,
                            StandardWatchEventKinds.ENTRY_MODIFY,
                            StandardWatchEventKinds.ENTRY_CREATE);
                    boolean poll = true;
                    log.info("File system listening started at path " + path);
                    log.info("App is initialized and ready");
                    while (poll) {
                        WatchKey key = watchService.take();
                        for (WatchEvent<?> event : key.pollEvents()) {
                            if ((clinicRepository.getName() + ".dat")
                                    .equals(event.context().toString())) {
                                clinicRepository.loadDataFromFile();
                            } else if ((userRepository.getName() + ".dat")
                                    .equals(event.context().toString())) {
                                userRepository.loadDataFromFile();
                            }
                        }
                        poll = key.reset();
                    }
                } catch (Exception e) {
                    log.error(e.toString());
                }
            });
        };
    }
}
