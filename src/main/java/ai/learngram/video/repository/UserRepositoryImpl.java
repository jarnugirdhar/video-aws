package ai.learngram.video.repository;

import ai.learngram.video.model.User;
import ai.learngram.video.repository.api.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserRepositoryImpl implements UserRepository {

    Map<String, User> store;

    public UserRepositoryImpl() {
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
