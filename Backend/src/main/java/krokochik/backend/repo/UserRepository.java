package krokochik.backend.repo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class UserRepository {
    private String storagePath = System.getProperty("java.io.tmpdir");
    private List<User> users = new ArrayList<>();

    @Autowired
    private ApplicationArguments args;

    @Autowired
    private Gson gson;

    @PostConstruct
    private void init() {
        if (args.getOptionValues("storage") != null)
            storagePath = args.getOptionValues("storage").get(0);
        if (args.getNonOptionArgs().contains("-renew") ||
                (args.getOptionValues("renew") != null &&
                        args.getOptionValues("renew").contains("users"))) {
            saveUsersToFile();
        } else loadUsersFromFile();
    }

    public void save(User user) {
        users.add(user);
        saveUsersToFile();
    }

    public User findByUsername(String username) {
        loadUsersFromFile();
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    public User[] getAllUsers() {
        return this.users.toArray(User[]::new);
    }

    @Async void saveUsersToFile() {
        log.info("Start exporting users");
        try (Writer writer = new FileWriter(storagePath + "/users.dat")) {
            gson.toJson(users, writer);
            log.info("Data is successfully written to " + storagePath + "/users.dat");
        } catch (IOException e) {
            log.error(e.toString());
        }
    }

    @Async void loadUsersFromFile() {
        log.info("Start importing users");
        try (Reader reader = new FileReader(storagePath + "/users.dat")) {
            User[] usersArray = gson.fromJson(reader, User[].class);
            if (usersArray != null) {
                users = new ArrayList<>(List.of(usersArray));
                log.info("Users are successfully read from file " + storagePath + "/users.dat");
            } else {
                log.error("Something went wrong during import");
            }
        } catch (IOException e) {
            log.error(e.toString());
        }
    }
}
