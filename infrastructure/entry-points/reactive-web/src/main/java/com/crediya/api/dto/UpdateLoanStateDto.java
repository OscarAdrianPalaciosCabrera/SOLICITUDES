package com.crediya.api.dto;

import java.util.UUID;

public record UpdateLoanStateDto(UUID loanRequestId,
                                 Integer newState) {
}
