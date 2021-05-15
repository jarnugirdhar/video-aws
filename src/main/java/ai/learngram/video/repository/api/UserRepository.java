package ai.learngram.video.repository.api;

import ai.learngram.video.model.User;

public interface UserRepository {

    boolean exists(User user);

    boolean save(User user);

    User getByEmail(String email);
}
