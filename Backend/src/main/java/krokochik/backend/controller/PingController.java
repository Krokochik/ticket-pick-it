package krokochik.backend.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class PingController {
    @GetMapping("/ping")
    public String pingGet() {
        return "pong";
    }

    @PostMapping("/ping")
    public String pingPost() {
        return "pong";
    }
}
