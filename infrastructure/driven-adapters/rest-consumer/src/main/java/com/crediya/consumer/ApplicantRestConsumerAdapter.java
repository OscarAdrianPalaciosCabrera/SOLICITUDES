package com.crediya.consumer;

import com.crediya.model.applicant.gateways.ApplicantRepository;
import com.crediya.model.loanrequest.gateways.LoanRequestRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class ApplicantRestConsumerAdapter implements ApplicantRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicantRestConsumerAdapter.class);
    private final RestConsumer restConsumer;

    @Override
    public Mono<Boolean> existsByIdentityDocument(String identityDocument) {
        LOGGER.debug("Entering to existsByIdentityDocument - identityDocument: {}", identityDocument);
        return restConsumer.getApplicantByIdentityDocument(identityDocument)
                .hasElement();
    }

}
