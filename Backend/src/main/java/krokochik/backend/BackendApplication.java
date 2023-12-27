package krokochik.backend;

import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync(proxyTargetClass = true)
public class BackendApplication {

    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

}
