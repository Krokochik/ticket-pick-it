package krokochik.backend.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import jakarta.servlet.http.HttpServletResponse;
import krokochik.backend.repo.UserRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;

@RestController
@RequestMapping(value = "/auth",
        produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/info")
    public String giveAuthInfo() {
        val securityContext = SecurityContextHolder.getContext();
        JsonObject json = new JsonObject();
        json.add("isAuth", new JsonPrimitive(!
                securityContext.getAuthentication()
                        .getAuthorities()
                        .contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));
        return json.toString();
    }

    @PostMapping(value = "/signup", produces = MediaType.TEXT_PLAIN_VALUE)
    public String register(@RequestBody HashMap<String, String> request,
                                   HttpServletResponse response) {
        final byte MIN_USERNAME_LENGTH = 4;
        final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";

        String username = request.get("username");
        String password = request.get("password");
        response.setStatus(HttpStatus.BAD_REQUEST.value());

        if (username == null || password == null) {
            return (username == null ? "Username" : "Password") +
                    " is required but missing";
        } else if (username.length() < MIN_USERNAME_LENGTH) {
            return "Min username length is " + MIN_USERNAME_LENGTH;
        } else if (!password.matches(PASSWORD_REGEX)) {
            return "Bad password";
        }

        if (userRepository.findByUsername(username) != null) {
            response.setStatus(HttpStatus.CONFLICT.value());
            return "Username is already taken";
        }

        userRepository.save(new User(username, password,
                Collections.singleton(new SimpleGrantedAuthority("USER"))));
        response.setStatus(HttpStatus.CREATED.value());
        return "Success";
    }
}
