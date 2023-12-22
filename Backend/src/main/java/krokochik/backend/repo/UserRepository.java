package krokochik.backend.repo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.annotation.Nullable;
import krokochik.backend.model.SerializableUser;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.boot.ApplicationArguments;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class UserRepository extends BaseRepositoryImpl<Set<SerializableUser>> {
    UserRepository(Gson gson,
                   ApplicationArguments args) {
        super(gson, args, new HashSet<>());
    }

    @Override
    public String getName() {
        return "users";
    }

    @Override
    protected Type getDataType() {
        return new TypeToken<Set<SerializableUser>>() {}.getType();
    }

    public void save(SerializableUser user) {
        data = data.stream()
                .filter(u -> !u.getUsername().equals(user.getUsername()))
                .collect(Collectors.toSet());
        data.add(user);
        saveDataToFile();
    }

    public void save(User user) {
        save(new SerializableUser(
                user.getUsername(), user.getPassword()));
        saveDataToFile();
    }

    @Nullable
    public SerializableUser findByUsername(String username) {
        loadDataFromFile();
        return data.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    public SerializableUser[] getAllUsers() {
        return this.data.toArray(SerializableUser[]::new);
    }

    private User[] toUsers(SerializableUser... sUsers) {
        User[] users = new User[sUsers.length];
        for (int i = 0; i < sUsers.length; i++) {
            val sUser = sUsers[i];
            if (sUser == null) {
                users[i] = null;
                continue;
            }
            users[i] = new User(
                    sUser.getUsername(),
                    sUser.getPassword(),
                    Collections.singleton(
                            new SimpleGrantedAuthority("USER")));
        }
        return users;
    }

    public User convertToUser(SerializableUser sUser) {
        return toUsers(sUser)[0];
    }

    public User[] convertToUser(SerializableUser... sUser) {
        return toUsers(sUser);
    }

    @Override
    public Future<Boolean> loadDataFromFile() {
        log.info("Start importing data");
        File file = new File(storagePath + "/" + getName() + ".dat");
        try (Reader reader = new FileReader(file)) {
            final SerializableUser[] usersArray = gson.fromJson(reader, SerializableUser[].class);
            if (usersArray != null) {
                data = new HashSet<>(Set.of(usersArray));
                log.info("Data imported from " + file.getAbsolutePath());
                return CompletableFuture.completedFuture(true);
            } else {
                log.warn("Something went wrong during import data from " + file.getAbsolutePath());
                return CompletableFuture.completedFuture(false);
            }
        } catch (FileNotFoundException e) {
            log.warn("Something went wrong during import data from " +
                    file.getAbsolutePath() + " : File not found");
            return CompletableFuture.completedFuture(false);
        } catch (Exception e) {
            log.error("Something went wrong during import data from " +
                    file.getAbsolutePath(), e);
            return CompletableFuture.completedFuture(false);
        }
    }
}