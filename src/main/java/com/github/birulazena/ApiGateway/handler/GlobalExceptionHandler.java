package com.github.birulazena.ApiGateway.handler;

import com.github.birulazena.ApiGateway.dto.response.error.ErrorResponse;
import com.github.birulazena.ApiGateway.dto.response.error.ValidationErrorResponse;
import com.github.birulazena.ApiGateway.exception.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public Mono<ResponseEntity<Object>> serviceExceptionHandler(ServiceException ex) {
        return Mono.just(
                ResponseEntity.status(ex.getStatus()).body(ex.getBody()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Mono<ResponseEntity<ValidationErrorResponse>> validExceptionHandler(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField(),
                        fieldError -> fieldError.getDefaultMessage()
                ));

        return Mono.just(
                ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new ValidationErrorResponse("Validation failed", errors))
        );
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> unexpectedErrorHandler(Exception ex) {
        return Mono.just(
                ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ErrorResponse("An unexpected error occurred. Please try again later"))
        );
    }




}
