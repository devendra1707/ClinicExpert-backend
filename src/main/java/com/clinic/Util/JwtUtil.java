package com.clinic.Util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
public class JwtUtil {

    private static final String SECRETE_KEY = "anil_prajapati_clinic_expert_jwt_secret_key_512_bits_long_secure";
    private static final int VALIDATE_TOKEN = 60;

    private Key getSingKey() {
        return Keys.hmacShaKeyFor(SECRETE_KEY.getBytes(StandardCharsets.UTF_8));
    }
    public String getUserNameFromToken(String token) {
        log.info("===GetUserNameFromToken===");
        return getClaimsFromToken(token, Claims::getSubject);
    }

    private <T> T getClaimsFromToken(String token, Function<Claims, T> claimsResolver) {
        log.info("==GetClaimsFromTokens===");
        Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public Claims getAllClaimsFromToken(String token) {
        log.info("===GetClaimsFromToken===");
        return Jwts.parserBuilder().setSigningKey(SECRETE_KEY.getBytes()).build().parseClaimsJws(token).getBody();
    }

    public boolean validToken(String token, UserDetails userDetails) {
        log.info("===ValidToken===");
        String userName = getUserNameFromToken(token);
        return (userName.equals(userDetails.getUsername()) && !isExpirationDate(token));
    }

    public boolean isExpirationDate(String token) {
        log.info("===isExpiration Date Time===");
        final Date expirationDate = expiredDateFromToken(token);
        return expirationDate.before(new Date());
    }

    private Date expiredDateFromToken(String token) {
        log.info("===ExpirationDateFromToken===");
        return getClaimsFromToken(token, Claims::getExpiration);
    }

    public String generateToken(UserDetails userDetails) {
        log.info("===Token Generate====");
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder().setClaims(claims).setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + VALIDATE_TOKEN * 1000))
                .signWith(getSingKey(),SignatureAlgorithm.HS512).compact();
    }

}
