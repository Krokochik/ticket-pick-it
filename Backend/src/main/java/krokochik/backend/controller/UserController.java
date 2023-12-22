package krokochik.backend.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.http.HttpServletResponse;
import krokochik.backend.model.SerializableUser;
import krokochik.backend.repo.UserRepository;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Log4j2
@RestController
public class UserController {

    @Autowired
    Gson gson;

    @Autowired
    UserRepository userRepository;

    @PostMapping("/my-tickets")
    public String getMyTickets(HttpServletResponse response) {
        val auth = SecurityContextHolder.getContext().getAuthentication();
        val user = userRepository.findByUsername(auth.getName());
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "[]";
        }
        return gson.toJson(user.getBookedTickets(),
                new TypeToken<List<SerializableUser.STicket>>() {}.getType());
    }
}
