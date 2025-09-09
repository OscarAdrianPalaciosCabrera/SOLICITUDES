package com.crediya.model.loantype.gateways;

import com.crediya.model.loantype.LoanType;
import reactor.core.publisher.Mono;

public interface LoanTypeRepository {
    public Mono<LoanType> findByLoanType(String loanType);
}
