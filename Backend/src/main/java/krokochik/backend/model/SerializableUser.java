package krokochik.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SerializableUser {
    @NonNull
    String username;
    @NonNull
    String password;

    @Getter
    @AllArgsConstructor
    public static class STicket {
        int clinicId;
        Speciality speciality;
        int medicId;
        LocalDateTime date;
    }

    List<STicket> bookedTickets;
}
