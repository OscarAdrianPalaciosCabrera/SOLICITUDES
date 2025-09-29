package com.crediya.usecase.getpendingloanrequests;

import com.crediya.model.applicant.Applicant;
import com.crediya.model.applicant.gateways.ApplicantRepository;
import com.crediya.model.loanrequest.DomainLoanRequestsDTO;
import com.crediya.model.loanrequest.LoanRequest;
import com.crediya.model.loanrequest.gateways.LoanRequestRepository;
import com.crediya.model.loantype.LoanType;
import com.crediya.model.loantype.gateways.LoanTypeRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class GetPendingLoanRequestsUseCase{

    private final LoanRequestRepository loanRequestRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final ApplicantRepository applicantRepository;
    private static final Logger LOGGER = Logger.getLogger(GetPendingLoanRequestsUseCase.class.getName());

    public Flux<DomainLoanRequestsDTO> execute(int page, int size, List<Integer> states){
        LOGGER.info("Entering to execute mehtod with States: " + states);
    return  loanRequestRepository.findByStateIn(states)
            .skip((long) page * size)
            .take(size)
            .flatMap(this::toDto);

    }

    public Mono<DomainLoanRequestsDTO> toDto(LoanRequest request){
        LOGGER.info("Entering to toDTO mehtod - request: " + request);
        Mono<Applicant> applicantMono = applicantRepository.findByIdentityDocumentApplicant(request.getIdentityDocumentApplicant())
                .doOnNext(applicant -> LOGGER.info("Applicant found: "+ applicant))
                .doOnError(err -> LOGGER.warning("Error fetching applicant"));
        Mono<LoanType> loanTypeMono = loanTypeRepository.findByLoanType(request.getLoanType())
                .doOnNext(loanType -> LOGGER.info("LoanType found: "+ loanType))
                .doOnError(err -> LOGGER.warning("Error fetching loanType"));
        LOGGER.info("Entering to toDto mehtod - applicantMono: " + applicantMono + " loanTypeMono: " + loanTypeMono);
        return Mono.zip(applicantMono , loanTypeMono)
                .map(tuple ->{
                    Applicant applicant = tuple.getT1();
                    LoanType loanType = tuple.getT2();

                    BigDecimal i = BigDecimal.valueOf(loanType.getInterestRate()).multiply(BigDecimal.valueOf(0.01));
                    int n = request.getTimeLimit();

                    //Cuote=P⋅(1+i)n−1i(1+i)n  ==  Cuote= p(((i(1+i))^n)/((i+n)^-1))

                    BigDecimal numerator = i.multiply((BigDecimal.ONE.add(i)).pow(n));
                    BigDecimal denominator = ((BigDecimal.ONE.add(i)).pow(n)).subtract(BigDecimal.ONE);
                    BigDecimal fraction = numerator.divide(denominator, 10, RoundingMode.HALF_UP);

                    BigDecimal monthlyAmount= request.getAmount()
                            .multiply(fraction)
                            .setScale(2, RoundingMode.HALF_UP);


                    return  new DomainLoanRequestsDTO(
                            applicant.getEmail(),
                            applicant.getName(),
                            request.getLoanType(),
                            request.getAmount(),
                            request.getTimeLimit(),
                            loanType.getInterestRate(),
                            request.getState(),
                            applicant.getBaseSalary(),
                            monthlyAmount
                    );
                });
    }
}
