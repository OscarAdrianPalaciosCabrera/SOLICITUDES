package com.crediya.consumer;

import lombok.*;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder(toBuilder = true)
public class ApplicantResponse {
    private String name;
    private String email;
    private String identityDocumentApplicant;
    private BigDecimal baseSalary;
}
