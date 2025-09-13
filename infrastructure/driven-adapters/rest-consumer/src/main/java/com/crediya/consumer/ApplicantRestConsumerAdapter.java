package com.crediya.consumer;

import com.crediya.model.applicant.Applicant;
import com.crediya.model.applicant.gateways.ApplicantRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class ApplicantRestConsumerAdapter implements ApplicantRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicantRestConsumerAdapter.class);
    private final RestConsumer restConsumer;

    @Override
    public Mono<Applicant> findByIdentityDocumentApplicant(String identityDocumentApplicant) {
        LOGGER.debug("Entering to existsByIdentityDocument - identityDocument: {}", identityDocumentApplicant);
        return restConsumer.findByIdentityDocumentApplicant(identityDocumentApplicant);
    }

}
