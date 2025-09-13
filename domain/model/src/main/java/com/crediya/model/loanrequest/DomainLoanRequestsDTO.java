package com.crediya.model.loanrequest;

import java.math.BigDecimal;

public record DomainLoanRequestsDTO(String email, //user
                                    String name, //user

                                    String loanType, //loanType

                                    BigDecimal amount, //loanReq
                                    Double timeLimit, //loanReq

                                    Double interestRate, //loanType

                                    Integer state, //loanreq

                                    BigDecimal baseSalary, // user
                                    BigDecimal monthlyAmount ) {
}
