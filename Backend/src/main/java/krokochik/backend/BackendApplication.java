package krokochik.backend;

import com.github.javafaker.Faker;
import lombok.SneakyThrows;
import lombok.val;
import org.openjdk.jol.info.ClassLayout;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SpringBootApplication
@EnableAsync(proxyTargetClass = true)
public class BackendApplication {

    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

}
