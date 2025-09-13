package com.crediya.model.applicant;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Applicant {
    private String identityDocumentApplicant;
    private Boolean exists;
    private String email;
    private String name;
    private BigDecimal baseSalary;
}
