package com.crediya.model.applicant.gateways;

import com.crediya.model.applicant.Applicant;
import reactor.core.publisher.Mono;

public interface ApplicantRepository {
    Mono<Applicant> findByIdentityDocumentApplicant(String identityDocumentApplicant);
}
