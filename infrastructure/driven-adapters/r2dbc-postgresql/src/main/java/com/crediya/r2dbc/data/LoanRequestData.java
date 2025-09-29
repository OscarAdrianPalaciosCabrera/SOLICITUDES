package com.crediya.r2dbc.data;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table("loan_request")
public class LoanRequestData {

    @Id
    private UUID id;
    private BigDecimal amount;
    private String identityDocumentApplicant;
    private int timeLimit;
    private String loanType;
    private boolean autoValidation;
    private int state;
}
