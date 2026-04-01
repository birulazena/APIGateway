package com.github.birulazena.ApiGateway.service;

import com.github.birulazena.ApiGateway.dto.request.CreateUserRequestDto;
import com.github.birulazena.ApiGateway.dto.request.RegisterRequestDto;
import com.github.birulazena.ApiGateway.dto.request.UserRequestDto;
import com.github.birulazena.ApiGateway.dto.response.UserResponseDto;
import com.github.birulazena.ApiGateway.dto.response.error.ErrorResponse;
import com.github.birulazena.ApiGateway.exception.ServiceException;
import com.github.birulazena.ApiGateway.mapper.UserMapper;
import com.github.birulazena.ApiGateway.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final WebClient userClient;

    private final WebClient authClient;

    private final UserMapper userMapper;

    private final JwtService jwtService;

    public Mono<UserResponseDto> registerUser(CreateUserRequestDto dto) {
        UserRequestDto userRequestDto = userMapper.toUserRequestDto(dto);
        RegisterRequestDto registerRequestDto = userMapper.toRegisterRequestDto(dto);
        String token = jwtService.generateAdminAccessToken();

        return createUser(userRequestDto, token)
                .flatMap(userResponseDto -> authUser(registerRequestDto.addUserId(userResponseDto.id()))
                        .thenReturn(userResponseDto)
                        .onErrorResume(e -> deleteUser(userResponseDto.id(), token)
                                .retryWhen(Retry.backoff(3, Duration.ofSeconds(3)))
                                .then(Mono.error(e))));

    }

    private Mono<UserResponseDto> createUser(UserRequestDto userRequestDto, String token) {
        return handleResponse(userClient.post()
                .header("Authorization", "Bearer " + token)
                .bodyValue(userRequestDto), UserResponseDto.class);
    }

    private Mono<Void> deleteUser(Long userId, String token) {
        return handleResponse(userClient.delete()
                .uri("/{id}", userId)
                .header("Authorization", "Bearer " + token),
                Void.class);
    }

    private Mono<Void> authUser(RegisterRequestDto registerRequestDto) {
        return handleResponse(authClient.post()
                .uri("/public/register")
                .bodyValue(registerRequestDto), Void.class);
    }

    private <T> Mono<T> handleResponse(WebClient.RequestHeadersSpec<?> request, Class<T> tClass) {
        return request.exchangeToMono(response -> {
            if(response.statusCode().is2xxSuccessful()) {
                return response.bodyToMono(tClass);
            } else {
                return response.bodyToMono(Object.class)
                        .defaultIfEmpty("")
                        .flatMap(body -> Mono.error(
                                new ServiceException(response.statusCode(), body)
                        ));
            }
        })
                .onErrorMap(WebClientRequestException.class, e ->
                        new ServiceException(HttpStatus.SERVICE_UNAVAILABLE,
                                "Service unavailable: " + e.getMessage()));
    }
}
