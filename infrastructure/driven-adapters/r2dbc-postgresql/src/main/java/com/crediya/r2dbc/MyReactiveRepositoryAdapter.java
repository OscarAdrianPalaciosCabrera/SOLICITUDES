package com.crediya.r2dbc;
import com.crediya.model.loanrequest.DomainLoanRequestsDTO;
import com.crediya.model.loanrequest.LoanRequest;
import com.crediya.model.loanrequest.gateways.LoanRequestRepository;
import com.crediya.r2dbc.data.LoanRequestData;
import com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public class MyReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        LoanRequest,
        LoanRequestData,
        String,
        MyReactiveRepository>
implements LoanRequestRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyReactiveRepositoryAdapter.class);


    public MyReactiveRepositoryAdapter(MyReactiveRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, LoanRequest.class/* change for domain model */));
    }

    @Override
    public Mono<LoanRequest> saveLoanRequest(LoanRequest loanRequest) {
        LOGGER.debug("Entering to saveLoanRequest method - loanRequest: {}", loanRequest.getIdentityDocumentApplicant());
        return repository.save(mapper.map(loanRequest, LoanRequestData.class))
                .map(data -> mapper.map(data, LoanRequest.class));
    }

    @Override
    public Flux<LoanRequest> findByStateIn(List<Integer> state) {
        LOGGER.debug("Finding LoanRequests by states: {}", state);
        return repository.findByStateIn(state)
                .map(this::toEntity);
    }

}
