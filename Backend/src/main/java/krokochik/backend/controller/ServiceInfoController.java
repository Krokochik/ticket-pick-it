package krokochik.backend.controller;

import jakarta.servlet.http.HttpServletResponse;
import krokochik.backend.model.Clinic;
import krokochik.backend.model.Medic;
import krokochik.backend.model.Speciality;
import krokochik.backend.repo.ClinicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.Inet4Address;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ServiceInfoController {

    @Autowired
    ClinicRepository clinicRepository;

    @GetMapping("/speciality/{code}/name")
    public String getSpecialityNameByCode(@PathVariable String code,
                                          HttpServletResponse response) {
        try {
            return Speciality.valueOf(code.toUpperCase()).fullName();
        } catch (IllegalArgumentException e) {
            response.setStatus(404);
            return "Speciality with specified code not found";
        }
    }

    @GetMapping("/speciality/{name}/code")
    public String getSpecialityCodeByName(@PathVariable String name,
                                          HttpServletResponse response) {
        try {
            return Arrays.stream(Speciality.values()).filter(
                    sp -> sp.fullName().equals(name)
            ).findFirst().orElseThrow(NoSuchFieldException::new).name();
        } catch (NoSuchFieldException e) {
            response.setStatus(404);
            return "Speciality with specified name not found";
        }
    }

    @GetMapping("/medic/name")
    public Map<String, String> getMedicNameById(@RequestParam("clinicId") String clinicIdVar,
                                                @RequestParam("speciality") String specialityCode,
                                                @RequestParam("id") String medicIdVar,
                                                HttpServletResponse response) {
        int medicId = -1;

        try {
            medicId = Integer.parseInt(medicIdVar);
            int clinicId = Integer.parseInt(clinicIdVar);
            Speciality speciality = Speciality.valueOf(specialityCode.toUpperCase());

            Clinic clinic = clinicRepository.getData().get(clinicId);
            List<Medic> medics = OrderController
                    .getMedicsWithSpeciality(clinic, speciality);

            if (medicId >= medics.size()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return new HashMap<>() {{
                   put("error", String
                           .format("Could not find medic with id: %s", medicIdVar));
                }};
            }

            Medic medic = medics.get(medicId);
            return new HashMap<>() {{
                put("firstName", medic.getFirstName());
                put("lastName", medic.getLastName());
            }};
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            int finalMedicId = medicId;
            return new HashMap<>() {{
                put("error", String
                        .format("Numeric id was expected but '%s' given: ",
                                finalMedicId == -1 ? medicIdVar : clinicIdVar));
            }};
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return new HashMap<>() {{
                put("error", String
                        .format("Speciality code was expected but '%s' given", specialityCode));
            }};
        } catch (IndexOutOfBoundsException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return new HashMap<>() {{
                put("error", String
                        .format("Could not find clinic with id: %s", clinicIdVar));
            }};
        }
    }
}
