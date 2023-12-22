package krokochik.backend.config;

import com.google.gson.*;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

import java.lang.reflect.Type;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

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
    public JsonSerializer<PersistentRememberMeToken> rememberMeTokenSerializer() {
        return (PersistentRememberMeToken src, Type typeOfSrc,
                JsonSerializationContext context) -> {
            if (src == null) return null;
            JsonObject json = new JsonObject();
            json.add("username", new JsonPrimitive(src.getUsername()));
            json.add("series", new JsonPrimitive(src.getSeries()));
            json.add("token", new JsonPrimitive(src.getTokenValue()));
            json.add("date", new JsonPrimitive(LocalDateTime
                    .ofInstant(src.getDate().toInstant(), ZoneId.systemDefault()).toString()));
            return json;
        };
    }

    @Bean
    public JsonDeserializer<PersistentRememberMeToken> rememberMeTokenDeserializer() {
        return (JsonElement element, Type typeOfT,
                JsonDeserializationContext context) -> {
            if (element == null) return null;
            val json = element.getAsJsonObject();
            return new PersistentRememberMeToken(
                    json.get("username").getAsString(),
                    json.get("series").getAsString(),
                    json.get("token").getAsString(),
                    Date.valueOf(LocalDateTime.parse(json.get("date").getAsString()).toLocalDate())
            );
        };
    }

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, dateSerializer())
                .registerTypeAdapter(LocalDateTime.class, dateDeserializer())
                .registerTypeAdapter(Time.class, timeSerializer())
                .registerTypeAdapter(Time.class, timeDeserializer())
                .registerTypeAdapter(PersistentRememberMeToken.class, rememberMeTokenSerializer())
                .registerTypeAdapter(PersistentRememberMeToken.class, rememberMeTokenDeserializer())
                .setLenient()
                .create();
    }
}
