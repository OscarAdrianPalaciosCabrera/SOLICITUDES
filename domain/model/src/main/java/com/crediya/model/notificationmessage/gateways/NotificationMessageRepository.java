package com.crediya.model.notificationmessage.gateways;
import com.crediya.model.notificationmessage.NotificationMessage;
import reactor.core.publisher.Mono;

public interface NotificationMessageRepository {
    Mono<String> publish(NotificationMessage event);
}
