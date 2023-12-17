package krokochik.backend.repo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.boot.ApplicationArguments;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Type;
import java.util.*;

@Slf4j
@Repository
public class RememberMeTokenRepository extends BaseRepositoryImpl<Set<PersistentRememberMeToken>> implements PersistentTokenRepository {
    public RememberMeTokenRepository(Gson gson,
                                     ApplicationArguments args) {
        super(gson, args, new HashSet<>());
    }

    @Override
    protected String getName() {
        return "remember-me";
    }

    @Override
    protected Type getDataType() {
        return new TypeToken<Set<PersistentRememberMeToken>>() {}.getType();
    }

    @Override
    public void createNewToken(PersistentRememberMeToken token) {
        data.add(token);
        saveDataToFile();
    }

    @Override
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        for (val token : data) {
            if (series.equals(token.getSeries())) {
                data.remove(token);
                createNewToken(new PersistentRememberMeToken(
                        token.getUsername(),
                        series, tokenValue,
                        lastUsed
                ));
                break;
            }
        }
    }

    @Override
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
        for (val token : data) {
            if (seriesId.equals(token.getSeries())) {
                return token;
            }
        }
        return null;
    }

    @Override
    public void removeUserTokens(String username) {
        data.removeIf(token -> username.equals(token.getUsername()));
        saveDataToFile();
    }
}
