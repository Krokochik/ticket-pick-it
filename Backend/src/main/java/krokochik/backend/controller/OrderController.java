package krokochik.backend.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.http.HttpServletResponse;
import krokochik.backend.model.*;
import krokochik.backend.repo.ClinicRepository;
import krokochik.backend.repo.UserRepository;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@RestController
public class OrderController {

    @Autowired
    Gson gson;

    @Autowired
    ClinicRepository clinicRepository;

    @Autowired
    UserRepository userRepository;

    record Result(List<LocalDateTime> res, String err) { }

    @PostMapping("/book-ticket")
    public String bookTicket(@RequestBody Map<String, String> body, HttpServletResponse response) {
        String clinicId = body.get("clinicId");
        String speciality = body.get("speciality");
        String medicId = body.get("medicId");
        String dateVar = body.get("date");

        if (clinicId == null || speciality == null || medicId == null || dateVar == null) {
            return handleBadRequest(response).err;
        }

        speciality = speciality.toUpperCase();

        try {
            Result dates = getAvailableTicketDates(clinicId, speciality, medicId, response);
            if (dates.res == null) {
                return dates.err;
            }

            LocalDateTime dateTime = parseDateTime(dateVar, response);
            if (dateTime == null) {
                return String.format("Bad date given: %s", dateVar);
            }

            if (bookTicket(clinicId, speciality, medicId, dateTime)) {
                return "Successfully booked";
            } else {
                response.setStatus(HttpServletResponse.SC_FOUND);
                return "Something went wrong";
            }
        } catch (NumberFormatException e) {
            return handleBadRequest(response, clinicId, medicId).err;
        } catch (IllegalArgumentException e) {
            return handleSpecialityError(response, speciality).err;
        }
    }

    @PostMapping("/unbook-ticket")
    public String unbookTicket(@RequestBody Map<String, String> body, HttpServletResponse response) {
        String clinicId = body.get("clinicId");
        String speciality = body.get("speciality");
        String medicId = body.get("medicId");
        String dateVar = body.get("date");

        if (clinicId == null || speciality == null || medicId == null || dateVar == null) {
            return handleBadRequest(response).err;
        }

        Result dates = getAvailableTicketDates(clinicId, speciality, medicId, response);
        if (dates.res == null) {
            return dates.err;
        }

        return unbookTicket(clinicId, speciality, medicId, dateVar, response);
    }

    private boolean bookTicket(String clinicId, String speciality, String medicId, LocalDateTime dateTime) {
        for (val date : getAvailableTicketDates(clinicId, speciality, medicId, null).res) {
            if (date.toString().equals(dateTime.toString())) {
                Clinic clinic = clinicRepository.getData().get(Integer.parseInt(clinicId));
                List<Medic> medics = getMedicsWithSpeciality(clinic, Speciality.valueOf(speciality.toUpperCase()));
                clinic.getEmployees().forEach(employee -> {
                    if (employee.getMedic().equals(medics.get(Integer.parseInt(medicId)))) {
                        bookTicketForEmployee(employee, clinicId, speciality, medicId, dateTime);
                    }
                });
                return true;
            }
        }
        return false;
    }

    private void bookTicketForEmployee(Employee employee, String clinicId, String speciality, String medicId, LocalDateTime dateTime) {
        Specialist specialist = employee.getSpecialist();
        List<Ticket> tickets = specialist.getTickets();
        tickets.forEach(ticket -> {
            if (ticket.getDate().equals(dateTime)) {
                ticket.setBooked(true);
                SerializableUser user = getCurrentUser();
                if (user.getBookedTickets() == null) {
                    user.setBookedTickets(new ArrayList<>());
                }
                user.getBookedTickets().add(new SerializableUser.STicket(
                        Integer.parseInt(clinicId),
                        Speciality.valueOf(speciality),
                        Integer.parseInt(medicId), dateTime
                ));
                userRepository.saveDataToFile();
            }
        });
        specialist.setTickets(tickets);
        employee.setSpecialist(specialist);
        clinicRepository.saveDataToFile();
    }

    @GetMapping("/clinic/{clinicVar}/speciality/{specialityVar}/medic/{medicVar}/tickets")
    public String getAvailableTickets(@PathVariable String clinicVar,
                                      @PathVariable String specialityVar,
                                      @PathVariable String medicVar,
                                      HttpServletResponse response) {
        try {
            Result dates = getAvailableTicketDates(clinicVar, specialityVar, medicVar, response);
            if (dates.res == null) return dates.err;

            return gson.toJson(dates.res, new TypeToken<List<LocalDateTime>>() {
            }.getType());
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return String.format("One or more of given ids are bad: %s, %s",
                    clinicVar, medicVar);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return String.format("Speciality code was expected but '%s' given",
                    specialityVar);
        }
    }

    private String unbookTicket(String clinicId, String speciality, String medicId, String dateVar, HttpServletResponse response) {
        val user = getCurrentUser();
        val ticketsAfterUnbooking = user.getBookedTickets().stream().filter(ticket ->
                !(ticket.getClinicId() == Integer.parseInt(clinicId) &&
                        ticket.getSpeciality().equals(Speciality.valueOf(speciality)) &&
                        ticket.getMedicId() == Integer.parseInt(medicId) &&
                        ticket.getDate().toString().equals(dateVar))).collect(Collectors.toList());

        if (ticketsAfterUnbooking.size() == user.getBookedTickets().size()) {
            response.setStatus(404);
            return "Could not find ticket with given params";
        }

        user.setBookedTickets(ticketsAfterUnbooking);
        userRepository.save(user);
        Medic medic = getMedicsWithSpeciality(clinicRepository.getData().get(Integer.parseInt(clinicId)),
                Speciality.valueOf(speciality)).get(Integer.parseInt(medicId));

        clinicRepository.getData().get(Integer.parseInt(clinicId)).getEmployees().forEach(employee -> {
            if (employee.getMedic().equals(medic)) {
                employee.getSpecialist().getTickets().forEach(ticket -> {
                    if (ticket.getDate().toString().equals(dateVar)) {
                        ticket.setBooked(false);
                    }
                });
            }
        });

        clinicRepository.saveDataToFile();
        return "Successfully unbooked";
    }

    private Result getAvailableTicketDates(String clinicVar,
                                           String specialityVar,
                                           String medicVar,
                                           HttpServletResponse response) {
        int clinicId = Integer.parseInt(clinicVar),
                medicId = Integer.parseInt(medicVar);
        if (clinicId < 0 || medicId < 0) {
            return handleIllegalIdError(response, clinicId, medicId);
        }

        val clinics = clinicRepository.getData();
        if (clinicId >= clinics.size()) {
            return handleNotFoundError(response, String.format("Cannot find clinic with id: %d", clinicId));
        }
        Clinic clinic = clinics.get(clinicId);
        Speciality speciality = Speciality.valueOf(specialityVar.toUpperCase());
        List<Medic> medics = getMedicsWithSpeciality(clinic, speciality);

        if (medics.size() <= medicId) {
            return handleNotFoundError(response, String.format("Cannot find medic with id: %d", medicId));
        }

        Medic medic = medics.get(medicId);
        Specialist specialist = new Specialist();
        clinic.getEmployees().forEach(employee -> {
            if (employee.getMedic().equals(medic))
                specialist.setTickets(employee
                        .getSpecialist().getTickets());
        });

        specialist.setTickets(specialist.getTickets().stream()
                .filter(ticket -> !ticket.isBooked()).toList());
        ArrayList<LocalDateTime> dates = new ArrayList<>();
        specialist.getTickets().forEach(ticket ->
                dates.add(ticket.getDate()));
        return new Result(dates, null);
    }

    static List<Medic> getMedicsWithSpeciality(Clinic clinic, Speciality sp) {
        List<Medic> medics = new ArrayList<>();
        clinic.getEmployees().forEach(employee -> {
            if (employee.getSpecialist().getSpeciality().equals(sp)) {
                val medic = employee.getMedic();
                medic.setSpecialities(null);
                medics.add(medic);
            }
        });
        return medics;
    }

    private Result handleIllegalIdError(HttpServletResponse response, int clinicId, int medicId) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return new Result(null, String.format("Illegal clinic id or medic id given: %d, %d", clinicId, medicId));
    }

    private Result handleNotFoundError(HttpServletResponse response, String message) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return new Result(null, message);
    }

    private Result handleBadRequest(HttpServletResponse response, String... params) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return new Result(null, String
                .format("One or more of given ids are bad: %s", Arrays.toString(params)));
    }

    private Result handleSpecialityError(HttpServletResponse response, String specialityVar) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return new Result(null, String
                .format("Speciality code was expected but '%s' given", specialityVar));
    }

    private LocalDateTime parseDateTime(String dateVar, HttpServletResponse response) {
        try {
            return LocalDateTime.parse(dateVar);
        } catch (Exception e) {
            log.error(e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
    }

    private SerializableUser getCurrentUser() {
        return userRepository.findByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
