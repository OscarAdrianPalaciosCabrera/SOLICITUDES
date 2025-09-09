package com.crediya.consumer;

import com.crediya.model.applicant.gateways.ApplicantRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor

public class RestConsumer implements ApplicantRepository{

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicantRestConsumerAdapter.class);
    private final WebClient client;


    // these methods are an example that illustrates the implementation of WebClient.
    // You should use the methods that you implement from the Gateway from the domain.

    /*
    @CircuitBreaker(name = "testGet" , fallbackMethod = "testGetOk")
    public Mono<ObjectResponse> testGet() {
        return client
                .get()
                .retrieve()
                .bodyToMono(ObjectResponse.class);
    }
    */
// Possible fallback method
//    public Mono<String> testGetOk(Exception ignored) {
//        return client
//                .get() // TODO: change for another endpoint or destination
//                .retrieve()
//                .bodyToMono(String.class);
//    }

    /*
    @CircuitBreaker(name = "testPost")
    public Mono<ObjectResponse> testPost() {
        ObjectRequest request = ObjectRequest.builder()
            .val1("exampleval1")
            .val2("exampleval2")
            .build();
        return client
                .post()
                .body(Mono.just(request), ObjectRequest.class)
                .retrieve()
                .bodyToMono(ObjectResponse.class);
    }*/

    @CircuitBreaker(name = "applicantGet", fallbackMethod = "fallbackApplicantExists")
    public Mono<ApplicantResponse>getApplicantByIdentityDocument(String identityDocument){
        LOGGER.debug("Entering to getApplicantByIdentityDocument method - identityDocument: {}", identityDocument);
        ApplicantRequest request = ApplicantRequest.builder()
                .identityDocument(identityDocument)
                .build();
        return  client.post()
                .uri("/api/v1/usuarios/existingByIdentityDocument")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, resp -> Mono.empty()) // 🔥 Manejo 404
                .bodyToMono(ApplicantResponse.class)
                .doOnNext(response -> System.out.println("Respuesta de ms-usuarios: " + response));

    }

    public Mono<ApplicantResponse> fallbackApplicantExists(String identityDocument, Throwable ex){
        LOGGER.debug("Entering to fallbackApplicantExists - identityDocument: {}, exception: {}", identityDocument, ex);
        ApplicantResponse response = new ApplicantResponse();
        response.setIdentityDocument(identityDocument);
        return Mono.just(response);
    }

    @Override
    public Mono<Boolean> existsByIdentityDocument(String identityDocument) {
        LOGGER.debug("Entering to existsByIdentityDocument method - identityDocument: {}", identityDocument);
        return getApplicantByIdentityDocument(identityDocument)
                .map(response -> response != null)
                .defaultIfEmpty(false);
    }
}
