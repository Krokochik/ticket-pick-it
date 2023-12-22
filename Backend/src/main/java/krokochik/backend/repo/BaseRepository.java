package krokochik.backend.repo;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.concurrent.Future;

@Repository
public interface BaseRepository {
    @Async void init();

    @Async
    Future<Boolean> saveDataToFile();

    @Async
    Future<Boolean> loadDataFromFile();
}
