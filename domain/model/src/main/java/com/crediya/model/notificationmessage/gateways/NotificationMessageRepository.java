package com.crediya.model.notificationmessage.gateways;
import com.crediya.model.loanrequest.CapacityCalculationPayLoad;
import com.crediya.model.loanrequest.DomainLoanRequestsDTO;
import com.crediya.model.notificationmessage.NotificationMessage;
import reactor.core.publisher.Mono;

public interface NotificationMessageRepository {
    Mono<String> publish(NotificationMessage event);
    Mono<Void> publish (CapacityCalculationPayLoad payLoad);
}
