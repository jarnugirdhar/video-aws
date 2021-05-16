package ai.learngram.video.repository;

import ai.learngram.video.model.User;
import ai.learngram.video.repository.api.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Repository
@Qualifier("localUserRepository")
public class LocalUserRepository implements UserRepository {

    Map<String, User> store;

    public LocalUserRepository() {
        store = new HashMap<>();
    }

    @Override
    public boolean exists(User user) {
        return store.containsKey(user.getEmail());
    }

    @Override
    public boolean save(User user) {
        store.put(user.getEmail(), user);
        return true;
    }

    @Override
    public User getByEmail(String email) {
        if(store.containsKey(email)) {
            return store.get(email);
        }
        return null;
    }
}
