package com.github.birulazena.ApiGateway.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@AllArgsConstructor
@Getter
@Setter
public class ServiceException extends RuntimeException {

    private HttpStatusCode status;

    private Object body;

}
