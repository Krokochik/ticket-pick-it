package krokochik.backend.config;

import krokochik.backend.repo.BaseRepository;
import krokochik.backend.repo.ClinicRepository;
import krokochik.backend.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
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
import java.util.concurrent.*;
import java.util.function.Function;

@Slf4j
@Configuration
@EnableAsync
@EnableCaching
public class RunConfigurer {

    @Autowired
    ApplicationArguments args;

    @Bean
    public ApplicationRunner runner() {
        return args -> {
            var temp = Paths.get(System.getProperty("java.io.tmpdir"));
            if (args.getOptionValues("storage") != null)
                temp = Paths.get(args.getOptionValues("storage").get(0));

            log.info("App is initialized and ready");
        };
    }

    @Bean
    Callable<ExecutorService> newExecutorService() {
        return () -> {
            val async = (args.containsOption("async") &&
                    !args.getOptionValues("async").contains("off")) ||
                    args.getNonOptionArgs().contains("-async");

            if (async) {
                int threadCount = (int) Math.round(
                        Runtime.getRuntime().availableProcessors() * 1.5);
                if (args.containsOption("thread-count")) {
                    val threadCountOption = args
                            .getOptionValues("thread-count")
                            .get(0);
                    try {
                        threadCount = Math.min(1024,
                                Integer.parseInt(threadCountOption));
                    } catch (NumberFormatException ignore) {
                    }
                }
                return Executors.newFixedThreadPool(threadCount);
            } else return Executors.newSingleThreadExecutor();
        };
    }
}
