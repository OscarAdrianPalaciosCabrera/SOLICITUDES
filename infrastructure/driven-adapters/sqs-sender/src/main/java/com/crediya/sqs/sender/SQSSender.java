package com.crediya.sqs.sender;

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
import software.amazon.awssdk.thirdparty.jackson.core.JsonProcessingException;

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
                .queueUrl(properties.queueUrl())
                .messageBody(message)
                .build();
    }

    @Override
    public Mono<String> publish(NotificationMessage event) {
        SendMessageRequest request = SendMessageRequest.builder()
                .queueUrl(properties.queueUrl())
                .messageBody(toJson(event))
                .messageGroupId("loan-requests")
                .messageDeduplicationId(UUID.randomUUID().toString())
                .build();

        return Mono.fromFuture(() -> client.sendMessage(request))
                .doOnNext(response -> log.info("Message sent to SQS - id: {}", response.messageId()))
                .doOnError(err -> log.error("Error publishing to SQS", err))
                .map(SendMessageResponse::messageId);


    }

    private String toJson(NotificationMessage event) {
        try {
            return mapper.writeValueAsString(event);
        } catch (Exception e) {
            throw new IllegalStateException("Error serializing NotificationMessage", e);
        }
    }
}
