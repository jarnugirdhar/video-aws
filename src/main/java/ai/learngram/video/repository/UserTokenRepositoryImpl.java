package ai.learngram.video.repository;

import ai.learngram.video.repository.api.UserTokenRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class UserTokenRepositoryImpl implements UserTokenRepository {

    Map<String, String> tokenStore;

    public UserTokenRepositoryImpl() {
        this.tokenStore = new HashMap<>();
    }

    @Override
    public String store(String id) {
        String token = String.valueOf(System.currentTimeMillis());
        tokenStore.put(token, id);
        return token;
    }

    @Override
    public String fetchByToken(String token) {
        if(tokenStore.containsKey(token)) {
            return tokenStore.get(token);
        }
        return null;
    }

    @Override
    public boolean invalidate(String token) {
        if(tokenStore.containsKey(token)) {
            tokenStore.remove(token);
            return true;
        }
        return false;
    }
}
