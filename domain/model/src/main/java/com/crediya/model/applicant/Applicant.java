package com.crediya.model.applicant;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Applicant {
    private String identityDocument;
    private Boolean exists;
}
