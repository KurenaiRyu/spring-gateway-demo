package io.github.natsusai.gateway.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JWTFilter implements GlobalFilter, Ordered {

    private static final String WWW_AUTH_HEADER = "WWW-Authenticate";
    private static final String X_JWT_SUB_HEADER = "X-jwt-sub";

    private static final Logger logger = LoggerFactory.getLogger(JWTFilter.class);

    private final JWTProperties jwtProperties;
    private final TokenProvider tokenProvider;

    public JWTFilter(JWTProperties jwtProperties,
        TokenProvider tokenProvider) {
        this.jwtProperties = jwtProperties;
        this.tokenProvider = tokenProvider;
    }


    //TODO: 处理错误返回
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            String token = this.extractJWTToken(exchange.getRequest());
            Jws<Claims> jws = tokenProvider.verifyToken(token);

            ServerHttpRequest request = exchange.getRequest().mutate().
                header(X_JWT_SUB_HEADER, jws.getBody().getSubject()).
                build();

            return chain.filter(exchange.mutate().request(request).build());


        } catch (JwtException ex) {

            logger.error(ex.getMessage());
            return this.onError(exchange, ex.getMessage());
        }
    }

    private String extractJWTToken(ServerHttpRequest request)
    {
        if (!request.getHeaders().containsKey("Authorization")) {
            throw new JWTTokenExtractException("Authorization header is missing");
        }

        List<String> headers = request.getHeaders().get("Authorization");

        if (headers == null || headers.isEmpty()) {
            throw new JWTTokenExtractException("Authorization header is empty");
        }

        String credential = headers.get(0).trim();
        String[] components = credential.split("\\s");

        if (components.length != 2) {
            throw new JWTTokenExtractException("Malformat Authorization content");
        }

        if (!components[0].equals("Bearer")) {
            throw new JWTTokenExtractException("Bearer is needed");
        }

        return components[1].trim();
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err)
    {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(WWW_AUTH_HEADER, this.formatErrorMsg(err));

        return response.setComplete();
    }

    private String formatErrorMsg(String msg)
    {
        return String.format("Bearer realm=\"acm.com\", " +
                "error=\"https://tools.ietf.org/html/rfc7519\", " +
                "error_description=\"%s\" ",  msg);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}