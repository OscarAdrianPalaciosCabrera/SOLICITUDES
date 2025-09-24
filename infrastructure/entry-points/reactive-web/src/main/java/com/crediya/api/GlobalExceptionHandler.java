package com.crediya.api;

import com.crediya.usecase.exceptions.BusinessExceptions;
import jakarta.validation.ConstraintViolationException;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GlobalExceptionHandler {

    public Mono<ServerResponse> handleConstraintViolation(ConstraintViolationException ex){
        var mensajes = ex.getConstraintViolations()
                .stream()
                .map(cv -> cv.getMessage())
                .collect(Collectors.toList());

        return ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "error", "Invalid input",
                        "messages", mensajes,
                        "status", 400,
                        "timestamp", LocalDateTime.now()
                ));
    }

    public Mono<ServerResponse> handleDeserializationException(ServerWebInputException ex){
        return ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "error", "Invalid input",
                        "message", ex.getReason(),
                        "status", 400,
                        "timestamp", LocalDateTime.now()

                ));
    }

    public Mono<ServerResponse> handleBusinessException(BusinessExceptions ex) {
        return ServerResponse.status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "error", ex.getMessage(),
                        "status", 409,
                        "timestamp", LocalDateTime.now()
                ));
    }

    public Mono<ServerResponse> handleGenericException(Throwable ex) {
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "error", "Internal server error",
                        "message", ex,
                        "status", 500,
                        "timestamp", LocalDateTime.now()
                ));
    }

}
