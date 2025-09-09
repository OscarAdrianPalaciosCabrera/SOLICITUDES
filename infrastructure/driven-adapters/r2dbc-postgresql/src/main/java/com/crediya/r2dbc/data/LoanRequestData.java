package com.crediya.r2dbc.data;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table("loan_request")
public class LoanRequestData {

    @Id
    private String id;
    private BigDecimal amount;
    private String identityDocumentApplicant;
    private Double timeLimit;
    private String loanType;
    private int state;
}
