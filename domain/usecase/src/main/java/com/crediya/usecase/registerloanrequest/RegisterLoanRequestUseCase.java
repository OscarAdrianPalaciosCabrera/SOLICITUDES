package com.crediya.usecase.registerloanrequest;

import com.crediya.model.applicant.gateways.ApplicantRepository;
import com.crediya.model.loanrequest.CapacityCalculationPayLoad;
import com.crediya.model.loanrequest.LoanRequest;
import com.crediya.model.loanrequest.gateways.LoanRequestRepository;
import com.crediya.model.loantype.gateways.LoanTypeRepository;
import com.crediya.model.notificationmessage.gateways.NotificationMessageRepository;
import com.crediya.usecase.getpendingloansforuser.GetUserPendingLoansUseCase;
import com.crediya.usecase.validationsloanrequest.ValidationsLoanRequestUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class RegisterLoanRequestUseCase {

    private final LoanRequestRepository loanApplicationRepository;
    private final ValidationsLoanRequestUseCase validationsLoanApplicationUseCase;
    private final GetUserPendingLoansUseCase getUserPendingLoansUseCase;
    private final ApplicantRepository applicantRepository;
    private final NotificationMessageRepository notificationMessageRepository;
    private final LoanTypeRepository loanTypeRepository;
    private static final Logger LOGGER = Logger.getLogger(RegisterLoanRequestUseCase.class.getName());


    public Mono<LoanRequest> saveLoanRequest(LoanRequest loanRequest, String emailLoggedClient) {
        LOGGER.info("Entering RegisterLoanRequestUseCase - saveLoanRequest method");

        return validationsLoanApplicationUseCase.existingLoanType(loanRequest.getLoanType())
                .then(validationsLoanApplicationUseCase.existingApplicant(loanRequest.getIdentityDocumentApplicant()))
                .then(validationsLoanApplicationUseCase.sameUser(loanRequest.getIdentityDocumentApplicant(), emailLoggedClient))
                .then(loanApplicationRepository.saveLoanRequest(loanRequest)) // guarda y devuelve LoanRequest
                .flatMap(saved -> {
                    if (saved.getAutoValidation()) {
                        return loanTypeRepository.findByLoanType(saved.getLoanType())
                                .flatMap(loanType ->
                                        applicantRepository.findByIdentityDocumentApplicant(saved.getIdentityDocumentApplicant()) // traemos applicant
                                                .flatMap(applicant -> // aquí sí existe la variable applicant
                                                        getUserPendingLoansUseCase.excecute(
                                                                        saved.getIdentityDocumentApplicant(),
                                                                        0,
                                                                        100,
                                                                        List.of(1)
                                                                )
                                                                .collectList()
                                                                .flatMap(approvedLoans -> {
                                                                    BigDecimal baseSalary = applicant.getBaseSalary() != null
                                                                            ? applicant.getBaseSalary()
                                                                            : BigDecimal.ZERO;

                                                                    CapacityCalculationPayLoad payload = new CapacityCalculationPayLoad(
                                                                            saved.getId(),
                                                                            saved.getIdentityDocumentApplicant(),
                                                                            emailLoggedClient,
                                                                            baseSalary, // usamos el salario real
                                                                            saved.getAmount(),
                                                                            loanType.getInterestRate(),
                                                                            saved.getTimeLimit(),
                                                                            approvedLoans.stream()
                                                                                    .map(l -> Map.of("monthlyAmount", l.monthlyAmount()))
                                                                                    .toList()
                                                                    );

                                                                    LOGGER.info("Publishing payload to capacity Lambda: " + payload);

                                                                    return notificationMessageRepository.publish(payload)
                                                                            .thenReturn(saved);
                                                                })
                                                )
                                );
                    } else {
                        return Mono.just(saved);
                    }
                });
    }
}
