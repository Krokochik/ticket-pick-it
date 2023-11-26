package krokochik.backend.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import lombok.val;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {


    @GetMapping("/auth/info")
    public String giveAuthInfo() {
        val securityContext = SecurityContextHolder.getContext();
        JsonObject json = new JsonObject();
        System.out.println(securityContext.getAuthentication().isAuthenticated());
        json.add("isAuth", new JsonPrimitive(!
                securityContext.getAuthentication()
                        .getAuthorities()
                        .contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));
        return json.toString();
    }
}
