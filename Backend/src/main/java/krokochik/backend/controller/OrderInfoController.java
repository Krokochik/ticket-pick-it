package krokochik.backend.controller;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import krokochik.backend.model.Clinic;
import krokochik.backend.model.Medic;
import krokochik.backend.model.Speciality;
import krokochik.backend.repo.ClinicRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class OrderInfoController {

    @Autowired
    ClinicRepository clinicRepository;

    @Autowired
    Gson gson;

    @GetMapping("/clinics")
    public List<Map<String, String>> clinicNames() {
        List<String> clinicNames = new ArrayList<>();
        List<String> clinicAddresses = new ArrayList<>();
        clinicRepository.getData().forEach(clinic -> {
            clinicNames.add(clinic.getName().replaceAll("\\{SPACE}", " "));
            clinicAddresses.add(clinic.getAddress().replaceAll("\\{SPACE}", " "));
        });

        List<Map<String, String>> result = new ArrayList<>();
        for (int i = 0; i < clinicNames.size(); i++) {
            int finalI = i;
            result.add(new HashMap<>() {{
                put("name", clinicNames.get(finalI));
                put("address", clinicAddresses.get(finalI));
            }});
        }

        return result;
    }

    @GetMapping("/clinic/{idVar}/specialities")
    public List<Map<String, String>> clinicSpecialities(@PathVariable String idVar,
                                                        HttpServletResponse response) {
        int id;
        Clinic clinic;

        try {
            id = Integer.parseInt(idVar);
            clinic = clinicRepository.getData().get(id);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } catch (IndexOutOfBoundsException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        List<Speciality> specialities = new ArrayList<>();
        clinic.getEmployees().forEach(employee -> {
            if (!specialities.contains(employee.getSpecialist().getSpeciality())) {
                specialities.add(employee.getSpecialist().getSpeciality());
            }
        });

        List<Map<String, String>> result = new ArrayList<>();
        for (val speciality : specialities) {
            result.add(new HashMap<>() {{
                put("name", speciality.fullName());
                put("code", speciality.name());
            }});
        }
        return result;
    }

    @GetMapping("/clinic/{idVar}/info")
    public Map<String, String> clinicInfo(@PathVariable String idVar,
                                          HttpServletResponse response) {
        int id;
        Clinic clinic;

        try {
            id = Integer.parseInt(idVar);
            clinic = clinicRepository.getData().get(id);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } catch (IndexOutOfBoundsException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        return new HashMap<>() {{
            put("name", clinic.getName().replaceAll("\\{SPACE}", " "));
            put("address", clinic.getAddress().replaceAll("\\{SPACE}", " "));
            put("phone", clinic.getPhone().replaceAll("\\{SPACE}", " "));
        }};
    }

    @GetMapping("/clinic/{idVar}/speciality/{spVar}/medics")
    public String getMedicsAtClinicBySpeciality(@PathVariable String idVar,
                                                @PathVariable String spVar,
                                                HttpServletResponse response) {
        spVar = spVar.toUpperCase();
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        int id = -1;
        Speciality sp;
        record Result(String firstName, String lastName,
                      String photo, int office) { }
        List<Result> medics = new ArrayList<>();

        try {
            id = Integer.parseInt(idVar);
            sp = Speciality.valueOf(spVar);
            Clinic clinic = clinicRepository.getData().get(id);
            clinic.getEmployees().forEach(employee -> {
                val specialist = employee.getSpecialist();
                val medic = employee.getMedic();
                if (specialist.getSpeciality().equals(sp)) {
                    medics.add(new Result(
                            medic.getFirstName(),
                            medic.getLastName(),
                            medic.getPhoto(),
                            specialist.getOffice()));
                }
            });
            if (medics.isEmpty()) throw new NullPointerException();
        } catch (NumberFormatException e) {
            return String.format("Bad id given: %s; A number was expected.", idVar);
        } catch (IllegalArgumentException e) {
            return String.format("Bad speciality given: %s; An enum code was expected.", spVar);
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return String.format("Cannot found a clinic with a such id: %d", id);
        }

        response.setStatus(HttpServletResponse.SC_OK);
        return gson.toJson(medics);
    }
}

