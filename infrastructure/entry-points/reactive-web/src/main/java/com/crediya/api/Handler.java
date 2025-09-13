package com.crediya.api;

import com.crediya.api.dto.CreateLoanRequestDTO;
import com.crediya.api.mapper.RequestDtoMapper;
import com.crediya.model.loanrequest.LoanRequest;
import com.crediya.usecase.exceptions.BusinessExceptions;
import com.crediya.model.loanrequest.DomainLoanRequestsDTO;
import com.crediya.usecase.getpendingloanrequests.GetPendingLoanRequestsUseCase;
import com.crediya.usecase.registerloanrequest.RegisterLoanRequestUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class Handler {

    private final RegisterLoanRequestUseCase registerLoanRequestUseCase;
    private final GetPendingLoanRequestsUseCase getPendingLoanRequestsUseCase;
    private final RequestDtoMapper mapper;
    private final Validator validator;
    private final GlobalExceptionHandler globalExceptionHandler;
    private static final Logger LOGGER = LoggerFactory.getLogger(Handler.class);

    public Mono<ServerResponse> listenPOSTRegisterLoanRequest(ServerRequest serverRequest) {
        LOGGER.debug("Entering to listenPOSTRegisterLoanRequest method - serverRequest: {}", serverRequest);

        return serverRequest.bodyToMono(CreateLoanRequestDTO.class)
                .flatMap(dto -> {
                    // 1. Validación de datos
                    Set<ConstraintViolation<CreateLoanRequestDTO>> violations = validator.validate(dto);
                    if (!violations.isEmpty()) {
                        return Mono.error(new ConstraintViolationException(violations));
                    }

                    // 2. Obtener usuario autenticado
                    return ReactiveSecurityContextHolder.getContext()
                            .map(SecurityContext::getAuthentication)
                            .flatMap(auth -> {
                                String emailLoggedUser = auth.getName();
                                String role = auth.getAuthorities().iterator().next().getAuthority();

                                // 3. Validación de rol
                                if (!"ROLE_CLIENT".equals(role)) {
                                    LOGGER.warn("User {} attempted to register a loan request without CLIENT role", emailLoggedUser);
                                    return Mono.error(new BusinessExceptions("Only clients can resgister Loan requests"));
                                }

                                // 4. Mapear y guardar
                                LoanRequest loanRequest = mapper.toModel(dto);
                                LOGGER.info("LoanRequest mapped to model -  dto {} - loanRequest : {} ", dto, loanRequest.getIdentityDocumentApplicant());
                                return registerLoanRequestUseCase.saveLoanRequest(loanRequest, emailLoggedUser);
                            });
                })
                // 5. Respuesta exitosa
                .flatMap(saved -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(saved))
                // 6. Manejo de errores
                .onErrorResume(ConstraintViolationException.class, globalExceptionHandler::handleConstraintViolation)
                .onErrorResume(ServerWebInputException.class, globalExceptionHandler::handleDeserializationException)
                .onErrorResume(BusinessExceptions.class, globalExceptionHandler::handleBusinessException)
                .onErrorResume(Throwable.class, globalExceptionHandler::handleGenericException);
    }

    public Mono<ServerResponse> listenGETLoanRequests(ServerRequest request) {
        int page = Integer.parseInt(request.queryParam("page").orElse("0"));
        int size = Integer.parseInt(request.queryParam("size").orElse("10"));

        LOGGER.info("Consultando solicitudes pendientes/rechazadas/manual - page={}, size={}", page, size);

        return ServerResponse.ok()
                .body(
                        getPendingLoanRequestsUseCase.execute(page, size)
                                .doOnNext(dto -> LOGGER.debug("Solicitud encontrada: {}", dto))
                                .doOnError(error -> LOGGER.error("Error al consultar solicitudes", error)),
                        DomainLoanRequestsDTO.class
                )
                .onErrorResume(ex -> {
                    LOGGER.error("Error controlado: {}", ex.getMessage());
                    return ServerResponse.status(500)
                            .bodyValue("{\"message\": \"No fue posible obtener las solicitudes\"}");
                });
    }
}
