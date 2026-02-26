package com.example.ddmdemo.security;

import com.example.ddmdemo.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TokenUtil {

    @Value("${app.name:web-shop-app}") // Dobra praksa je koristiti ${} format
    private String APP_NAME;

    @Value("${app.secret:somesecret}")
    public String SECRET;

    @Value("${app.expires_in:900000}")
    private int EXPIRES_IN;

    @Value("${app.auth_header:Authorization}")
    private String AUTH_HEADER;

    private SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

    public String generateToken(User user) {
        // Popravljeno: koristimo 'user' umesto 'merchant' i pretpostavljamo da User ima getEmail() i getRole()
        Claims claims = Jwts.claims().setSubject(user.getEmail());
        claims.put("role", user.getRole());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer(APP_NAME)
                .setSubject(user.getEmail()) // Popravljeno
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + EXPIRES_IN))
                .signWith(SIGNATURE_ALGORITHM, SECRET)
                .compact();
    }

    public String getRoleFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims != null ? claims.get("role", String.class) : null;
    }

    public String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTH_HEADER);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }

    public Boolean validateToken(String token, User user) {
        final String email = getEmailFromToken(token);
        final Date expiration = getExpiration(token);

        // Proveri da li tvoja User klasa koristi getEmail() ili getMerchantEmail()
        return (email != null
                && email.equals(user.getEmail())
                && expiration != null && expiration.after(new Date()));
    }

    public String getEmailFromToken(String token) {
        String email;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            email = claims != null ? claims.getSubject() : null;
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (Exception e) {
            email = null;
        }
        return email;
    }

    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (Exception e) {
            return null;
        }
    }

    public Date getExpiration(String token) {
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            return claims != null ? claims.getExpiration() : null;
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (Exception e) {
            return null;
        }
    }
}