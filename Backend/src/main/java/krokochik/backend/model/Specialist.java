package krokochik.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Specialist {
    Speciality speciality;
    Short office;
    Short minutesPerPatient;
    WorkingHours workingHours;
    List<Ticket> tickets;
    List<Date> holidays;
}
