package krokochik.backend.repo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import krokochik.backend.model.*;
import krokochik.backend.service.ClinicGenerator;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Slf4j
@Getter
@Service
public class ClinicRepository {
    private static final String DEFAULT_STORAGE_PATH = System.getProperty("java.io.tmpdir");
    private List<Clinic> clinics;

    @Autowired
    Gson gson;

    @Autowired
    ClinicGenerator clinicGenerator;

    public void generateClinics() {
        clinics = clinicGenerator.generateClinics();
    }

    @Async
    @SneakyThrows
    public boolean exportClinics(String path) {
        if (path == null)
            path = DEFAULT_STORAGE_PATH;
        File file = new File(path + "/clinics.dat");
        try (FileWriter writer = new FileWriter(file)) {
            writer.append(gson.toJson(clinics,
                    new TypeToken<List<Clinic>>() {}.getType()));
            log.info("Data successfully exported to " + file.getAbsolutePath());
            return true;
        } catch (Exception e) {
            log.error(e.toString());
            return false;
        }
    }

    @SneakyThrows
    public boolean importClinics(String path) {
        if (path == null || path.isBlank() ||
                (!new File(path).canRead() && !new File(path).canWrite())) {
            path = DEFAULT_STORAGE_PATH;
        }
        File file = new File(path + "/clinics.dat");
        if (!file.exists()) return false;
        try (Scanner scanner = new Scanner(file)) {
            StringBuilder fileContent = new StringBuilder();
            while (scanner.hasNext())
                fileContent.append(scanner.next());
            clinics = gson.fromJson(fileContent.toString(),
                    new TypeToken<List<Clinic>>() {}.getType());
            log.info("Data imported from " + file.getAbsolutePath());
            return true;
        } catch (Exception e) {
            log.error(e.toString());
            return false;
        }
    }
}
