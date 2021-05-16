package ai.learngram.video.controllers;

import ai.learngram.video.payload.RestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/")
    public ResponseEntity<?> healthCheck() {
        return new ResponseEntity<>(new RestResponse("Health success", 200), HttpStatus.OK);
    }

}
