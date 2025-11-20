package com.edunext.app.security;


import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.edunext.app.model.User;

@Service
public class TokenService {
    
    @Value("${api.security.token.secret}")
    private String secretKey;

    public String generateToken(User user){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            Instant expiresAt = Instant.now().plus(5, ChronoUnit.DAYS);
            String token = JWT.create()
                            .withIssuer("API EducaMais")
                            .withSubject(user.getUsername())
                            .withExpiresAt(Date.from(expiresAt))
                            .sign(algorithm);
            return token;
        }catch (JWTCreationException exception){
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    public String validateToken(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secretKey);

            return JWT.require(algorithm)
                    .withIssuer("API EducaMais")
                    .build()
                    .verify(token)
                    .getSubject();
        }
        catch (JWTVerificationException exception){
            return "";
        }
    }
}
