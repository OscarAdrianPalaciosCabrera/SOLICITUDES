package com.crediya.sqs.sender;

import com.crediya.model.loanrequest.CapacityCalculationPayLoad;
import com.crediya.model.notificationmessage.NotificationMessage;
import com.crediya.model.notificationmessage.gateways.NotificationMessageRepository;
import com.crediya.sqs.sender.config.SQSSenderProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender implements NotificationMessageRepository {

    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;
    private final ObjectMapper mapper = new ObjectMapper();


    private SendMessageRequest buildRequest(String message) {
        return SendMessageRequest.builder()
                .messageBody(message)
                .build();
    }

    @Override
    public Mono<String> publish(NotificationMessage event) {
        SendMessageRequest request = SendMessageRequest.builder()
                .queueUrl(properties.queues().get("loanRequests"))
                .messageBody(toJson(event))
                .messageGroupId("loan-requests")
                .messageDeduplicationId(UUID.randomUUID().toString())
                .build();

        return Mono.fromFuture(() -> client.sendMessage(request))
                .doOnNext(response -> log.info("Message sent to SQS - id: {}", response.messageId()))
                .doOnError(err -> log.error("Error publishing to SQS", err))
                .map(SendMessageResponse::messageId);


    }

    @Override
    public Mono<Void> publish(CapacityCalculationPayLoad payLoad) {
        SendMessageRequest request = SendMessageRequest.builder()
                .queueUrl(properties.queues().get("autoVerification"))
                .messageBody(toJson(payLoad))
                .messageGroupId("autoverification-loan-requests")
                .messageDeduplicationId(UUID.randomUUID().toString())
                .build();

        return Mono.fromFuture(() -> client.sendMessage(request))
                .doOnNext(response -> log.info("Message sent to SQS - id: {}", response.messageId()))
                .doOnError(err -> log.error("Error publishing to SQS", err))
                .then(); // retorna Mono<Void>
    }


    private String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new IllegalStateException("Error serializing NotificationMessage", e);
        }
    }

}
