package krokochik.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Data
@AllArgsConstructor
public class Clinic {
    String address;
    String phone;
    List<Employee> employees;
    List<Date> additionalWeekends;

    public Clinic() {
        employees = new ArrayList<>();
    }
}
