package com.crediya.usecase.getpendingloansforuser;

import com.crediya.model.loanrequest.DomainLoanRequestsDTO;
import com.crediya.model.loanrequest.gateways.LoanRequestRepository;
import com.crediya.usecase.getpendingloanrequests.GetPendingLoanRequestsUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class GetUserPendingLoansUseCase {

    private  final LoanRequestRepository loanRequestRepository;
    private final GetPendingLoanRequestsUseCase getPendingLoanRequestsUseCase;

    private static Logger LOGGER = Logger.getLogger(GetUserPendingLoansUseCase.class.getName());

    public Flux<DomainLoanRequestsDTO>excecute(String identityDocumentApplicant, int page, int size, List<Integer>states){
        LOGGER.info("Entering execute method with applicant: " + identityDocumentApplicant + " and states: " + states);
        return loanRequestRepository.findByIdentityDocumentAndStateIn(identityDocumentApplicant, states)
                .skip((long) page*size)
                .take(size)
                .flatMap(getPendingLoanRequestsUseCase::toDto);
    }
}
