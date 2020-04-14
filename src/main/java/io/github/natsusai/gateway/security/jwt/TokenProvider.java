package io.github.natsusai.gateway.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author liufuhong
 * @since 2020-04-14 16:55
 */

public class TokenProvider implements InitializingBean {

  private final JWTProperties jwtProperties;

  private Key key;
  private long tokenValidityInMilliseconds;
  private long tokenValidityInMillisecondsForRememberMe;

  public TokenProvider(JWTProperties jwtProperties) {
    this.jwtProperties = jwtProperties;
  }

  @Override
  public void afterPropertiesSet() {
    byte[] keyBytes;
    String secret = jwtProperties.getSecret();
    if (StringUtils.isNotEmpty(secret)) {
      keyBytes = secret.getBytes(StandardCharsets.UTF_8);
    } else {
      keyBytes = Base64.getDecoder().decode(jwtProperties.getBase64Secret());
    }
    this.key = Keys.hmacShaKeyFor(keyBytes);
    this.tokenValidityInMilliseconds =
        1000 * Optional.ofNullable(jwtProperties.getTokenValidityInSeconds()).orElse(360 * 3L);
    this.tokenValidityInMillisecondsForRememberMe =
        1000 * Optional.ofNullable(jwtProperties.getTokenValidityInSecondsForRememberMe())
            .orElse(360 * 24 * 3L);
  }

  public String createToken(String username, String userId, boolean rememberMe) {
    long now = (new Date()).getTime();
    Date validity;
    if (rememberMe) {
      validity = new Date(now + this.tokenValidityInMillisecondsForRememberMe);
    } else {
      validity = new Date(now + this.tokenValidityInMilliseconds);
    }
    return Jwts.builder()
        .signWith(key, SignatureAlgorithm.HS256)
        .setId(UUID.randomUUID().toString())
        .setIssuer("IotLead-Bee-X")
        .setSubject(userId)
        .setAudience(username)
        .setIssuedAt(new Date())
        .setExpiration(validity)
        .compact();
  }

  //TODO: 缓存（可以考虑用缓存作为验证手段，即不在缓存中不放行）
  public Jws<Claims> verifyToken(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
  }
}
