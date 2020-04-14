package io.github.natsusai.gateway.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;

class JWTFilterTest {

  @Test
  void jwtTest() {

    String secret = "123123123123sjdfkjkajsdfjlklasdj";




    SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

    String token = Jwts.builder().setSubject("Joe").signWith(key).compact();
    System.out.println("token = " + token);

    Jws<Claims> jws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    System.out.println("jws.getBody().getSubject() = " + jws.getBody().getSubject());
  }
}