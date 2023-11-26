package krokochik.backend;

import com.github.javafaker.Faker;
import lombok.SneakyThrows;
import lombok.val;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Locale;

@SpringBootApplication
public class BackendApplication {

    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

}
