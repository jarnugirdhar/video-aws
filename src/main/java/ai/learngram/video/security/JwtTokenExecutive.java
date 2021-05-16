package ai.learngram.video.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenExecutive {

    @Value("${auth.jwt.secret}")
    private String secret;

    @Value("${auth.jwt.expiration}")
    private int expiration;

    public String generateToken(Authentication authentication) {

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime() + expiration);

        return Jwts
                .builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(currentDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public String getPayloadSubject(String token) {
        Claims claims = Jwts
                .parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        }
        catch(Exception ex) {
            // Log.
        }
        return false;
    }

}
