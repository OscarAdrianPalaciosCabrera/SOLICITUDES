package com.crediya.model.loanrequest;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.logging.Logger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanRequest {

    private static final Logger LOGGER = Logger.getLogger(LoanRequest.class.getName());

    private UUID id;
    private BigDecimal amount;
    private String identityDocumentApplicant;
    private int timeLimit;
    private String loanType;
    private Boolean autoValidation;
    private int state = 0;

    public static LoanRequest create(UUID id, BigDecimal amount, String identityDocumentApplicant, int timeLimit, String loanType, Boolean autoValidation, int state ){
        LOGGER.info("Entering to create LoanRequest method in Model");
        return new LoanRequest(id, amount, identityDocumentApplicant, timeLimit, loanType, autoValidation, state);
    }

}
