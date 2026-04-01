package com.github.birulazena.ApiGateway.security.filter;

import com.github.birulazena.ApiGateway.dto.response.error.ErrorResponse;
import com.github.birulazena.ApiGateway.exception.InvalidTokenException;
import com.github.birulazena.ApiGateway.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter implements WebFilter {

    private final JwtService jwtService;

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String tokenHead = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if(tokenHead == null || !tokenHead.startsWith("Bearer "))
            return chain.filter(exchange);

        String token = tokenHead.substring(7);

        if(!jwtService.validateToken(token))
            return writeError(exchange, HttpStatus.UNAUTHORIZED, "Invalid token");

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                null,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + jwtService.getRole(token)))
        );

        auth.setDetails(jwtService.getDetails(token));

        return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
    }

    private Mono<Void> writeError(ServerWebExchange exchange, HttpStatus status, String message) {
        var response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ErrorResponse errorResponse = new ErrorResponse(message);

        byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
        var buffer = response.bufferFactory().wrap(bytes);

        return response.writeWith(Mono.just(buffer));
    }
}
