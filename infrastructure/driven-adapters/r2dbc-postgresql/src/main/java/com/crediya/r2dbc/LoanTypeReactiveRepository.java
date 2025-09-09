package com.crediya.r2dbc;
import com.crediya.r2dbc.data.LoanTypeData;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface LoanTypeReactiveRepository extends ReactiveCrudRepository<LoanTypeData, String>, ReactiveQueryByExampleExecutor<LoanTypeData> {
    Mono<LoanTypeData> findByLoanType(String loanType);
}
