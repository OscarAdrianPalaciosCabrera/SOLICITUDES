/*package com.crediya.kafkaconsumer;

import com.crediya.model.applicantcreatedevent.ApplicantCreatedEvent;
import com.crediya.r2dbc.ApplicantEntity;
import com.crediya.r2dbc.ApplicantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicantEventListener {
    private final ApplicantRepository applicantRepository;


    @KafkaListener(topics ="applicant-created-topic", groupId = "ms-loans-group")
    public void consume(ApplicantCreatedEvent event){
        ApplicantEntity entity = new ApplicantEntity(
                event.getIdentityDocumentApplicant()
        );
        applicantRepository.save(entity)
                .doOnSuccess(saved -> System.out.println("Applicant persisted: "+event.getIdentityDocumentApplicant()))
                .doOnError(err -> System.err.println("Error persisting applicant: "+ err.getMessage()))
                .subscribe();
    }

}
*/