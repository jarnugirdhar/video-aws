package ai.learngram.video.security;

import ai.learngram.video.model.User;
import ai.learngram.video.repository.api.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class ConcreteUserDetailsService implements UserDetailsService {

    @Autowired
    @Qualifier("localUserRepository")
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRepository.getByEmail(s);
        if(user == null) {
            throw new UsernameNotFoundException("Could not retrieve user with email: " + s);
        }
        return UserPrincipal.create(user);
    }
}
