package com.crediya.consumer;
import com.crediya.model.applicant.Applicant;
import com.crediya.model.applicant.gateways.ApplicantRepository;
import com.crediya.usecase.exceptions.BusinessExceptions;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpStatusCode;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RestConsumer implements ApplicantRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestConsumer.class);
    private final WebClient client;

    @CircuitBreaker(name = "applicantGet", fallbackMethod = "fallbackApplicantExists")
    public Mono<ApplicantResponse> getApplicantByIdentityDocument(String identityDocumentApplicant) {
        LOGGER.debug("Entering to getApplicantByIdentityDocument method - identityDocument: {}", identityDocumentApplicant);

        ApplicantRequest request = ApplicantRequest.builder()
                .identityDocumentApplicant(identityDocumentApplicant)
                .build();

        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> (Jwt) ctx.getAuthentication().getPrincipal())
                .map(Jwt::getTokenValue)
                .flatMap(jwtToken ->
                        client.post()
                                .uri("/api/v1/usuarios/existingByIdentityDocument")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                                .bodyValue(request)
                                .retrieve()
                                .onStatus(status -> status.is4xxClientError(), response -> {
                                    if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                                        return Mono.empty();
                                    } else if (response.statusCode().equals(HttpStatus.CONFLICT)) {
                                        return Mono.error(new BusinessExceptions(
                                                "Users can only create loan applications for themselves, not for other users. Logged user doesn't match the request user."));
                                    }
                                    return Mono.error(new BusinessExceptions(
                                            "Error calling ms-users: " + response.statusCode()));
                                })
                                .bodyToMono(ApplicantResponse.class)
                );
    }

    public Mono<ApplicantResponse> fallbackApplicantExists(String identityDocumentApplicant, Throwable ex) {
        LOGGER.warn("Fallback triggered for identityDocument {}: {}", identityDocumentApplicant, ex.getMessage());
        return Mono.error(new BusinessExceptions("Cannot fetch applicant at the moment, try again later"));
    }

    @Override
    public Mono<Applicant> findByIdentityDocumentApplicant(String identityDocumentApplicant) {
        LOGGER.debug("Entering to existsByIdentityDocument method - identityDocument: {}", identityDocumentApplicant);

        return getApplicantByIdentityDocument(identityDocumentApplicant)
                .map(applicantResponse -> {
                    Applicant applicant = new Applicant();
                    applicant.setIdentityDocumentApplicant(applicantResponse.getIdentityDocumentApplicant());
                    applicant.setEmail(applicantResponse.getEmail());
                    applicant.setName(applicantResponse.getName());
                    applicant.setBaseSalary(applicantResponse.getBaseSalary());
                    return applicant;
                });
    }
}

