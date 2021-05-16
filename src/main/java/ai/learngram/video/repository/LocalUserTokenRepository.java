package ai.learngram.video.repository;

import ai.learngram.video.repository.api.UserTokenRepository;
import ai.learngram.video.utils.TokenGenerator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Qualifier("localUserTokenRepository")
public class LocalUserTokenRepository implements UserTokenRepository {

    Map<String, String> tokenStore;

    public LocalUserTokenRepository() {
        this.tokenStore = new ConcurrentHashMap<>();
    }

    @Override
    public String store(String id) {
        String token = TokenGenerator.generate();
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
