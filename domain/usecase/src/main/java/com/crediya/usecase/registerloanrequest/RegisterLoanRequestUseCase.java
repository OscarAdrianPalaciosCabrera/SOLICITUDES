package com.crediya.usecase.registerloanrequest;

import com.crediya.model.loanrequest.LoanRequest;
import com.crediya.model.loanrequest.gateways.LoanRequestRepository;
import com.crediya.usecase.validationsloanrequest.ValidationsLoanRequestUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@RequiredArgsConstructor
public class RegisterLoanRequestUseCase {

    private final LoanRequestRepository loanApplicationRepository;
    private final ValidationsLoanRequestUseCase validationsLoanApplicationUseCase;
    private static final Logger LOGGER = Logger.getLogger(RegisterLoanRequestUseCase.class.getName());


    public Mono<LoanRequest> saveLoanRequest(LoanRequest loanRequest, String emailLoggedClient){
        LOGGER.info("Entering RegisterLoanRequestUseCase  - saveLoanRequest method");
        return validationsLoanApplicationUseCase.existingLoanType(loanRequest.getLoanType())
                .then(validationsLoanApplicationUseCase.existingApplicant(loanRequest.getIdentityDocumentApplicant()))
                .then(validationsLoanApplicationUseCase.sameUser(loanRequest.getIdentityDocumentApplicant(), emailLoggedClient))
                .then(loanApplicationRepository.saveLoanRequest(loanRequest));
    }
}
