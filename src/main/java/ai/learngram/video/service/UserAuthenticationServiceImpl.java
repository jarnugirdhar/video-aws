package ai.learngram.video.service;


import ai.learngram.video.model.User;
import ai.learngram.video.repository.api.UserRepository;
import ai.learngram.video.security.JwtTokenExecutive;
import ai.learngram.video.service.api.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserAuthenticationServiceImpl implements UserAuthenticationService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenExecutive tokenExecutive;

    @Autowired
    UserRepository userRepository;

    @Override
    public String authorize(User user) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return tokenExecutive.generateToken(authentication);
    }

    @Override
    public boolean register(User user) {
        if(userRepository.exists(user)) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean exists(User user) {
        return false;
    }
}
