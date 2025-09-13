package com.crediya.model.loanrequest.gateways;
import com.crediya.model.loanrequest.DomainLoanRequestsDTO;
import com.crediya.model.loanrequest.LoanRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.util.List;

public interface LoanRequestRepository {

    Mono<LoanRequest> saveLoanRequest (LoanRequest loanRequest);
    Flux<LoanRequest> findByStateIn(List<Integer> state);
    //Flux<DomainLoanRequestsDTO> getPendingLoanRequests(int page, int size);

}
