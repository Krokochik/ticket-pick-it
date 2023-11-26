package krokochik.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Medic {
    List<Speciality> specialities;
    String firstName;
    String lastName;
    String photo;
}
