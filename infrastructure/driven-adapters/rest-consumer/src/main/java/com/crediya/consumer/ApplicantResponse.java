package com.crediya.consumer;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder(toBuilder = true)
public class ApplicantResponse {
    private String name;
    private String identityDocument;
}
