package ai.learngram.video.service;

import ai.learngram.video.config.SimpleMailMessageConfig;
import ai.learngram.video.model.User;
import ai.learngram.video.repository.api.UserRepository;
import ai.learngram.video.repository.api.UserTokenRepository;
import ai.learngram.video.security.JwtTokenExecutive;
import ai.learngram.video.service.api.UserAuthenticationService;
import ai.learngram.video.utils.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    @Qualifier("localUserRepository")
    UserRepository userRepository;

    @Autowired
    @Qualifier("localUserTokenRepository")
    UserTokenRepository userTokenRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    SimpleMailMessageConfig mailMessageConfig;

    @Override
    public String authorize(User user) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return tokenExecutive.generateToken(authentication);
    }

    @Override
    public boolean register(User user) {
        if(!Validator.validateEmail(user.getEmail()) || !Validator.validatePassword(user.getPassword())) {
            return false;
        }
        if(userRepository.exists(user)) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        String generatedToken = userTokenRepository.store(user.getEmail());
        emailService.sendEmail(mailMessageConfig.getDefaultMessage(user.getEmail(), generatedToken));
        return true;
    }

    @Override
    public boolean verify(String token) {
        String id = userTokenRepository.fetchByToken(token);
        if(id != null) {
            User user = userRepository.getByEmail(id);
            user.setEnabled(true);
            userRepository.save(user);
            userTokenRepository.invalidate(token);
            return true;
        }
        return false;
    }
}
