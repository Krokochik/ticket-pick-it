package krokochik.backend.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class SerializableUser {
    @NonNull
    String username;
    @NonNull
    String password;
}
