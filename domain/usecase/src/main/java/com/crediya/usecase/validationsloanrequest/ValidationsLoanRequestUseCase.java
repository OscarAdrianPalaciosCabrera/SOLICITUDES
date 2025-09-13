package com.crediya.usecase.validationsloanrequest;
import com.crediya.model.applicant.Applicant;
import com.crediya.model.applicant.gateways.ApplicantRepository;
import com.crediya.model.loanrequest.LoanRequest;
import com.crediya.model.loanrequest.gateways.LoanRequestRepository;
import com.crediya.model.loantype.gateways.LoanTypeRepository;
import com.crediya.usecase.exceptions.BusinessExceptions;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@RequiredArgsConstructor
public class ValidationsLoanRequestUseCase {

    private final LoanRequestRepository loanRequestRepository;
    private final LoanTypeRepository loanTypeRepository;
    //private final ApplicantCreatedEventRepository applicantCreatedEventRepository;
    private final ApplicantRepository applicantRepository;
    private static final Logger LOGGER = Logger.getLogger(ValidationsLoanRequestUseCase.class.getName());


    public Mono<Void> existingLoanType(String loanType){
        LOGGER.info("Entering to existingLoanType method");
        return loanTypeRepository.findByLoanType(loanType)
                .hasElement()
                .flatMap(existLoanType -> {
                    if (!existLoanType) {
                        LOGGER.warning("The Loan Type doesn't exist");
                        return Mono.error(new BusinessExceptions("The Loan Type doesn't exist"));
                    }
                    /*return applicantCreatedEventRepository.findById(loanApplication.getIdentityDocumentApplicant())
                            .hasElement()
                            .flatMap(existApplicant -> {
                                if (!existApplicant) {
                                    return Mono.error(new BusinessExceptions("The Applicant doesn't exist"));
                                }
                                return Mono.empty(); // todo OK
                            });*/
                    return  Mono.empty();
                });
    }

    public Mono<Boolean>existingApplicant(String identityDocument){
        LOGGER.info("Entering to existingApplicant method ");
        return applicantRepository.findByIdentityDocumentApplicant(identityDocument)
                .hasElement()
                .flatMap(existApplicant -> {
                    if(!existApplicant){
                        LOGGER.warning("The Applicant doesn´t exist");
                        return Mono.error(new BusinessExceptions("The Applicant doesn´t exist"));
                    }
                    return Mono.empty();
                });
    }

    public Mono<Boolean>sameUser(String identityDocument, String emailLoggedClient){
        LOGGER.info("Entering to sameUser method - identityDocument: " + identityDocument + " emailLoggedClient " + emailLoggedClient);
        return applicantRepository.findByIdentityDocumentApplicant(identityDocument)
                .flatMap(applicant -> {
                    LOGGER.info("Applicant request: " + applicant.getEmail());

                    if(applicant.getEmail().equals(emailLoggedClient)){
                        return Mono.just(true);
                    }else {
                        LOGGER.warning("The Logged user " + emailLoggedClient + " doesn´t match with the request user " + applicant.getEmail());
                        return Mono.error(new BusinessExceptions("Users can only create loan applications for themselves, not for other users."));
                    }
                })
                .switchIfEmpty(Mono.error(new BusinessExceptions("Applicant not found")));
    }
}
