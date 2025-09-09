package com.crediya.model.loanrequest.gateways;
import com.crediya.model.loanrequest.LoanRequest;
import reactor.core.publisher.Mono;

public interface LoanRequestRepository {

    Mono<LoanRequest> saveLoanRequest (LoanRequest loanRequest);
}
