package com.crediya.model.loanrequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record CapacityCalculationPayLoad(UUID id,
                                         String identityDocument,
                                         String email,
                                         BigDecimal baseSalary,
                                         BigDecimal loanAmount,
                                         Double interestRate,
                                         int timeLimit,
                                         List<Map<String, BigDecimal>> approvedLoans) {

}
