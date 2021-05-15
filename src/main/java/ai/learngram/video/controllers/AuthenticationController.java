package ai.learngram.video.controllers;


import ai.learngram.video.model.User;
import ai.learngram.video.payload.LoginRequest;
import ai.learngram.video.payload.RestResponse;
import ai.learngram.video.payload.SignupRequest;
import ai.learngram.video.service.api.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    UserAuthenticationService userAuthenticationService;

    @CrossOrigin(origins = "*")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {
        User user = new User(signupRequest.getName(), signupRequest.getEmail(), signupRequest.getPassword());
        boolean registrationStatus = userAuthenticationService.register(user);
        if(registrationStatus) {
            return new ResponseEntity<>(new RestResponse("Successfully registered.", 200), HttpStatus.OK);
        }
        return new ResponseEntity<>(new RestResponse("Failed to register. Something went wrong", 400), HttpStatus.BAD_REQUEST);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        User user = new User(loginRequest.email, loginRequest.password);
        String token = userAuthenticationService.authorize(user);
        return new ResponseEntity<>(new RestResponse(token, 200), HttpStatus.OK);
    }

}
