package krokochik.backend.config;

import com.github.javafaker.Faker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

@Configuration
public class FakerConfigurer {
    @Bean
    Faker faker() {
        return new Faker(new Locale("en-US"));
    }
}
