package com.github.birulazena.ApiGateway.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;
import java.util.List;

public record UserRequestDto(String name,
                             String surname,
                             LocalDate birthDate,
                             String email,
                             List<PaymentCardRequestDto> cards) {
}
