package com.crediya.api.dto;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateLoanRequestDTO(
        @NotBlank(message="Some mandatory field can not be blank")
        @NotNull(message="Some mandatory field can not be null")
        String identityDocumentApplicant,

        @NotNull(message="Some mandatory field can not be null")
        BigDecimal amount,

        @NotNull(message="Some mandatory field can not be null")
        Double timeLimit,

        @NotBlank(message="Some mandatory field can not be blank")
        @NotNull(message="Some mandatory field can not be null")
        String loanType,

        int state
        ){
}
