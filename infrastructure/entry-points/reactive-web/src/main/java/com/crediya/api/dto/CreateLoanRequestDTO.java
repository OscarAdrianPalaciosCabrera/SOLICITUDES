package com.crediya.api.dto;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateLoanRequestDTO(

        UUID id,

        @NotBlank(message="Some mandatory field can not be blank")
        @NotNull(message="Some mandatory field can not be null")
        String identityDocumentApplicant,

        @NotNull(message="Some mandatory field can not be null")
        BigDecimal amount,

        @NotNull(message="Some mandatory field can not be null")
        Integer timeLimit,

        @NotBlank(message="Some mandatory field can not be blank")
        @NotNull(message="Some mandatory field can not be null")
        String loanType,

        @NotNull(message="Some mandatory field can not be null")
        boolean autoValidation,

        int state
        ){
}
