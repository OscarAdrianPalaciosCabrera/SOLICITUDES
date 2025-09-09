package com.crediya.r2dbc;

import com.crediya.model.loanrequest.LoanRequest;
import com.crediya.model.loantype.LoanType;
import com.crediya.model.loantype.gateways.LoanTypeRepository;
import com.crediya.r2dbc.data.LoanTypeData;
import com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class LoanTypeReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        LoanType,
        LoanTypeData,
        String,
        LoanTypeReactiveRepository
        > implements LoanTypeRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoanTypeReactiveRepositoryAdapter.class);


    public LoanTypeReactiveRepositoryAdapter(LoanTypeReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, LoanType.class/* change for domain model */));
    }

    @Override
    public Mono<LoanType> findByLoanType(String loanType) {
        LOGGER.debug("Entering to findByLoanType - loanType: {}", loanType);
        return repository.findByLoanType(loanType)
                .map(data -> mapper.map(data, LoanType.class));
    }
}
