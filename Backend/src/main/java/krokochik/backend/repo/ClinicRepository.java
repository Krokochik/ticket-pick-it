package krokochik.backend.repo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import krokochik.backend.model.*;
import krokochik.backend.service.ClinicGenerator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.boot.ApplicationArguments;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
@Repository
public class ClinicRepository extends BaseRepositoryImpl<List<Clinic>> {

    private final ClinicGenerator clinicGenerator;

    ClinicRepository(Gson gson,
                     ApplicationArguments args,
                     ClinicGenerator generator) {
        super(gson, args, new ArrayList<>());
        this.clinicGenerator = generator;
    }

    @Override
    public String getName() {
        return "clinics";
    }

    @Override
    protected Type getDataType() {
        return new TypeToken<List<Clinic>>() {
        }.getType();
    }

    @Override
    @SneakyThrows
    public void init() {
        val renew = args.getOptionValues("renew");
        boolean dataLoaded = false;
        if (!args.getNonOptionArgs().contains("-renew")
                && (renew == null || !renew.contains(getName()))) {
            dataLoaded = loadDataFromFile().get();
        }
        if (!dataLoaded) {
            log.info("Clinic generation started");
            data = clinicGenerator.generateClinics();
            saveDataToFile();
        }
    }
}
