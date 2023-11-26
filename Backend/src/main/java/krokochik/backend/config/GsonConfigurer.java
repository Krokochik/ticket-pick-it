package krokochik.backend.config;

import com.google.gson.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.lang.reflect.Type;
import java.sql.Time;
import java.time.LocalDateTime;

@Configuration
public class GsonConfigurer {

    @Bean
    public JsonSerializer<LocalDateTime> dateSerializer() {
        return (LocalDateTime src, Type typeOfSrc,
                JsonSerializationContext context) ->
                src == null ? null : new JsonPrimitive(src.toString());
    }

    @Bean
    public JsonDeserializer<LocalDateTime> dateDeserializer() {
        return (JsonElement json, Type typeOfT,
                JsonDeserializationContext context) ->
                json == null ? null : LocalDateTime.parse(json.getAsString());
    }

    @Bean
    public JsonSerializer<Time> timeSerializer() {
        return (Time src, Type typeOfSrc,
                JsonSerializationContext context) ->
                src == null ? null : new JsonPrimitive(src.toString());
    }

    @Bean
    public JsonDeserializer<Time> timeDeserializer() {
        return (JsonElement json, Type typeOfT,
                JsonDeserializationContext context) ->
                json == null ? null : Time.valueOf(json.getAsString());
    }

    @Bean
    public JsonSerializer<GrantedAuthority> authoritySerializer() {
        return (GrantedAuthority src, Type typeOfSrc,
                JsonSerializationContext context) ->
                src == null ? null : new JsonPrimitive(src.getAuthority());
    }

    @Bean
    public JsonDeserializer<GrantedAuthority> authorityDeserializer() {
        return (JsonElement json, Type typeOfT,
                JsonDeserializationContext context) ->
                json == null ? null : new SimpleGrantedAuthority(json.toString());
    }

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, dateSerializer())
                .registerTypeAdapter(LocalDateTime.class, dateDeserializer())
                .registerTypeAdapter(Time.class, timeSerializer())
                .registerTypeAdapter(Time.class, timeDeserializer())
                .registerTypeAdapter(GrantedAuthority.class, authoritySerializer())
                .registerTypeAdapter(GrantedAuthority.class, authorityDeserializer())
                .create();
    }
}
