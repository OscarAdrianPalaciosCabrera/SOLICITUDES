package com.crediya.model.applicant.gateways;

import reactor.core.publisher.Mono;

public interface ApplicantRepository {
    Mono<Boolean> existsByIdentityDocument(String identityDocument);
}
