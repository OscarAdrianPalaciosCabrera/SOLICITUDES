package com.crediya.api.dto;

import java.math.BigDecimal;

public record LoanApplicationsResponseDTO(String email,
                                          String name,
                                          String loanType,
                                          BigDecimal amount,
                                          Integer timeLimit,
                                          Double interestRate,
                                          Integer state,
                                          Double baseSalary,
                                          BigDecimal MonthlyAmountRequest) {
}
