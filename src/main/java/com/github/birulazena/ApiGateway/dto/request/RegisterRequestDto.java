package com.github.birulazena.ApiGateway.dto.request;

public record RegisterRequestDto(String password,
                                 String username,
                                 Long userId) {

    public RegisterRequestDto addUserId(Long userId) {
        return new RegisterRequestDto(
                password,
                username,
                userId);
    }
}
