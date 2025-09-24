package com.crediya.model.notificationmessage;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
//import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
//@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class NotificationMessage {
    private UUID loanId;
    private Integer state;
    private String identityDocument;
    private String message;
}
