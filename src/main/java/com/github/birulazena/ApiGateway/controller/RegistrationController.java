package com.github.birulazena.ApiGateway.controller;

import com.github.birulazena.ApiGateway.dto.request.CreateUserRequestDto;
import com.github.birulazena.ApiGateway.dto.response.UserResponseDto;
import com.github.birulazena.ApiGateway.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/register")
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    public Mono<ResponseEntity<UserResponseDto>> register(@RequestBody CreateUserRequestDto createUserRequestDto) {
        return registrationService.registerUser(createUserRequestDto)
                .map(result -> ResponseEntity.status(HttpStatus.CREATED).body(result));
    }
}
