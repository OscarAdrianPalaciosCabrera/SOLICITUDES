package com.crediya.consumer;
import com.crediya.usecase.updateloanstate.UpdateLoanStateUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateStateConsumer {
    private final ObjectMapper mapper;
    private final UpdateLoanStateUseCase updateLoanStateUseCase;
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateStateConsumer.class);

    @SqsListener(value = "${adapter.sqs.queues.updateState}")
    public void consume(String message) {
        try {
            UpdateStateDTO event = mapper.readValue(message, UpdateStateDTO.class);
            LOGGER.info("Received SQS message: {}", event);
            updateLoanStateUseCase.updateState(event.id(), event.decision())
                    .doOnError(error -> LOGGER.error("Error updating loan state", error))
                    .subscribe(saved -> LOGGER.info("Loan state updated successfully"));
        } catch (Exception e) {
            LOGGER.error("Error processing SQS message", e);
            throw new RuntimeException(e); // requeue si falla
        }
    }
}
