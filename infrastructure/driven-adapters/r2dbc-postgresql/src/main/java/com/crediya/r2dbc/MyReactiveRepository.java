package com.crediya.r2dbc;

import com.crediya.model.applicant.Applicant;
import com.crediya.model.loanrequest.LoanRequest;
import com.crediya.r2dbc.data.LoanRequestData;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

// TODO: This file is just an example, you should delete or modify it
public interface MyReactiveRepository extends ReactiveCrudRepository<LoanRequestData, String>, ReactiveQueryByExampleExecutor<LoanRequestData> {

    Mono<Applicant> findByIdentityDocumentApplicant(String identityDocumentApplicant);
    Flux<LoanRequestData> findByStateIn(List<Integer> state);
    Flux<LoanRequestData> findByIdentityDocumentApplicantAndStateIn(String identityDocumentApplicant, List<Integer> states);
}
