package io.github.natsusai.gateway.security.jwt;


import io.jsonwebtoken.JwtException;

public class JWTTokenExtractException extends JwtException {

    public JWTTokenExtractException(String message) {
        super(message);
    }
}