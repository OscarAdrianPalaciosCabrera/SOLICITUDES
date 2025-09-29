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

    public Mono<LoanRequest> updateState(UUID id, Integer state) {
        LOGGER.info("Entering to updateState - state: " + state);

        // Validación temprana del estado
        if (state != 1 && state != 2 && state != 3) {
            return Mono.error(new BusinessExceptions("Only states 1 (Approved) or 2 (Rejected) or 3(Manual Revision) are allowed"));
        }

        return loanRequestRepository.findByIdLoan(id)
                .switchIfEmpty(Mono.error(new BusinessExceptions("Loan not found")))
                .flatMap(loanRequest -> {
                    if (loanRequest.getState() != 0) {
                        return Mono.error(new BusinessExceptions("Loan must be pending for revision"));
                    }

                    loanRequest.setState(state);

                    return loanRequestRepository.saveLoanRequest(loanRequest)
                            .flatMap(saved -> {
                                LOGGER.info("Loan updated - new state: " + saved.getState());

                                // Solo publicamos si el estado es 1 o 2
                                NotificationMessage event = new NotificationMessage(
                                        saved.getId(),
                                        state,
                                        saved.getIdentityDocumentApplicant(),
                                        String.format("Loan %s updated to state %d", saved.getId(), state)
                                );

                                LOGGER.info("Publishing event to SQS - event: " + event);
                                return notificationMessageRepository.publish(event)
                                        .thenReturn(saved);
                            });
                });
    }

}
