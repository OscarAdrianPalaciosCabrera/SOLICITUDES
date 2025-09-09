package com.crediya.model.applicantcreatedevent.gateways;

import com.crediya.model.applicantcreatedevent.ApplicantCreatedEvent;
import reactor.core.publisher.Mono;

public interface ApplicantCreatedEventRepository {
    Mono<ApplicantCreatedEvent>findById(String identityDocumentApplicant);
}
