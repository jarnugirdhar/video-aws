package ai.learngram.video.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
public class SimpleMailMessageConfig {

    @Value("${auth.confirmation.url}")
    private String url;

    public SimpleMailMessage getDefaultMessage(String email, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Signup Verification - Video App [DNR]");
        message.setFrom("no-reply@gmail.com");
        message.setText("Kindly verify your account by clicking: " + url + "?token="+token);
        return message;
    }
}
