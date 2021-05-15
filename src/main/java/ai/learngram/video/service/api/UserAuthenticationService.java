package ai.learngram.video.service.api;

import ai.learngram.video.model.User;

public interface UserAuthenticationService {

    String authorize(User user);

    boolean register(User user);

    boolean exists(User user);
}
