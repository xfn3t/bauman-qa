package ru.bmstu.storing.outbox;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.bmstu.storing.client.AnalysisServiceClient;
import ru.bmstu.storing.service.WorkSubmissionService;

@DataJpaTest
@Import({ OutboxPublisher.class, ObjectMapper.class })
class OutboxPublisherTest {

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Autowired
    private OutboxPublisher outboxPublisher;

    @MockitoBean
    private AnalysisServiceClient analysisServiceClient;

    @MockitoBean
    private WorkSubmissionService workSubmissionService;

    @Test
    void shouldPublishWorkSubmittedEvent() {
        OutboxEvent event = new OutboxEvent();
        event.setAggregateId(UUID.randomUUID());
        event.setEventType(OutboxEventType.WORK_SUBMITTED);
        event.setPayload("{\"workId\":\"" + UUID.randomUUID() + "\"}");
        event.setPublished(false);
        outboxEventRepository.save(event);

        outboxPublisher.publishOutboxEvents();

        OutboxEvent updated = outboxEventRepository
            .findById(event.getId())
            .orElseThrow();
        assert updated.isPublished();
    }

    @Test
    void shouldSkipAlreadyPublished() {
        OutboxEvent event = new OutboxEvent();
        event.setAggregateId(UUID.randomUUID());
        event.setEventType(OutboxEventType.WORK_SUBMITTED);
        event.setPayload("{\"workId\":\"" + UUID.randomUUID() + "\"}");
        event.setPublished(true);
        outboxEventRepository.save(event);

        outboxPublisher.publishOutboxEvents();

        verify(analysisServiceClient, never()).triggerAnalysis(any());
    }
}
