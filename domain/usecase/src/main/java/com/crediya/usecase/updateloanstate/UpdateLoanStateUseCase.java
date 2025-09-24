package com.crediya.usecase.updateloanstate;

import com.crediya.model.loanrequest.LoanRequest;
import com.crediya.model.loanrequest.gateways.LoanRequestRepository;
import com.crediya.model.notificationmessage.NotificationMessage;
import com.crediya.model.notificationmessage.gateways.NotificationMessageRepository;
import com.crediya.usecase.exceptions.BusinessExceptions;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class UpdateLoanStateUseCase {

    private final LoanRequestRepository loanRequestRepository;
    private final NotificationMessageRepository notificationMessageRepository;
    private static final Logger LOGGER = Logger.getLogger(UpdateLoanStateUseCase.class.getName());

    public Mono<LoanRequest>updateState(UUID id, Integer state){
        LOGGER.info("Entering to updateState - state: " + state);
        return loanRequestRepository.findByIdLoan(id)
                .switchIfEmpty(Mono.error(new BusinessExceptions("Loan not found")))
                .flatMap(loanRequest -> {

                    if(loanRequest.getState() != 0){
                        return Mono.error(new BusinessExceptions("Loan must be pending of revision"));
                    }

                    loanRequest.setState(state);
                    return loanRequestRepository.saveLoanRequest(loanRequest)
                            .flatMap(saved -> {
                                LOGGER.info("Calling saveLoanRequest - new state: " + saved.getState());
                                if (state == 1 || state == 2) {
                                    NotificationMessage event = new NotificationMessage(
                                            saved.getId(),
                                            state,
                                            saved.getIdentityDocumentApplicant(),
                                            String.format("Loan %s updated to state %d", saved.getId(), state)
                                    );
                                    LOGGER.info("Calling publish event to SQS - event: " + event);
                                    return notificationMessageRepository.publish(event)
                                            .thenReturn(saved);
                                }
                                return Mono.just(saved);
                            });
                });
    }

}
