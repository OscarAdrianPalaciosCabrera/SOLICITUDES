package com.crediya.consumer;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ApplicantRequest {
    private String identityDocument;
}
