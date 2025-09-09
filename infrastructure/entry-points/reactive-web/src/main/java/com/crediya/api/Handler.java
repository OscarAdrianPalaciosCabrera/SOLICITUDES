package com.crediya.api;

import com.crediya.api.dto.CreateLoanRequestDTO;
import com.crediya.api.mapper.RequestDtoMapper;
import com.crediya.model.loanrequest.LoanRequest;
import com.crediya.usecase.exceptions.BusinessExceptions;
import com.crediya.usecase.registerloanrequest.RegisterLoanRequestUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private final RequestDtoMapper mapper;
    private final Validator validator;
    private final GlobalExceptionHandler globalExceptionHandler;
    private static final Logger LOGGER = LoggerFactory.getLogger(Handler.class);



    public Mono<ServerResponse> listenPOSTRegisterLoanRequest(ServerRequest serverRequest) {
        LOGGER.debug("Entering to listenPOSTRegisterLoanRequest method - serverRequest: {}", serverRequest);
        try{
            return serverRequest.bodyToMono(CreateLoanRequestDTO.class)
                    .flatMap(dto -> {
                        Set<ConstraintViolation<CreateLoanRequestDTO>> violations = validator.validate(dto);
                        if(!violations.isEmpty()){
                            return  Mono.error(new ConstraintViolationException(violations));
                        }
                        LoanRequest loanRequest = mapper.toModel(dto);
                        LOGGER.info("listenPOSTRegisterLoanRequest LoanRequest mapped to model");
                        return registerLoanRequestUseCase.saveLoanRequest(loanRequest);
                    })
                    .flatMap(saved -> ServerResponse.status(HttpStatus.CREATED)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(saved))
                    .onErrorResume(ConstraintViolationException.class, globalExceptionHandler::handleConstraintViolation)
                    .onErrorResume(ServerWebInputException.class , globalExceptionHandler::handleDeserializationException)
                    .onErrorResume(BusinessExceptions.class, globalExceptionHandler::handleBusinessException)
                    .onErrorResume(Throwable.class, globalExceptionHandler::handleGenericException);
        }catch (Exception e){
            LOGGER.error("Unexpected error in listenPOSTRegisterLoanRequest with serverRequest: {} :{}", serverRequest, e.getMessage(), e);
            return  Mono.error(e);
        }

    }
}
