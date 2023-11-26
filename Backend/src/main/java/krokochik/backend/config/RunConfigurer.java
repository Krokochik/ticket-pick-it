package krokochik.backend.config;

import krokochik.backend.repo.ClinicRepository;
import krokochik.backend.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Slf4j
@Configuration
public class RunConfigurer {

    @Autowired
    ClinicRepository clinicRepository;

    @Autowired
    UserRepository userRepository;

    @Bean
    public ApplicationRunner runner() {
        return args -> {
            val storage = args.getOptionValues("storage");
            val renew = args.getOptionValues("renew");
            boolean importState = false;
            if (!args.getNonOptionArgs().contains("-renew")
                && (renew == null || !renew.contains("clinics"))) {
                importState = clinicRepository.importClinics(storage == null
                        ? null : storage.get(0));
            }
            if (!importState) {
                clinicRepository.generateClinics();
                clinicRepository.exportClinics(storage == null
                        ? null : storage.get(0));
            }

            log.info("App is initialized and ready");
        };
    }
}
