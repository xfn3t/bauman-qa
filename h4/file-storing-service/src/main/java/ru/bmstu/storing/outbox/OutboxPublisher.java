package ru.bmstu.storing.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.bmstu.storing.client.AnalysisServiceClient;
import ru.bmstu.storing.service.WorkSubmissionService;
import ru.bmstu.storing.service.dto.WorkDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final AnalysisServiceClient analysisServiceClient;
    private final WorkSubmissionService workSubmissionService;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelayString = "${outbox.poll-interval-ms:5000}")
    @Transactional
    public void publishOutboxEvents() {
        List<OutboxEvent> events = outboxEventRepository.findByPublishedFalseOrderByCreatedAtAsc();

        for (OutboxEvent event : events) {
            try {
                if (event.getEventType() == OutboxEventType.WORK_SUBMITTED) {
                    Map<String, Object> payloadMap = objectMapper.readValue(
                            event.getPayload(),
                            objectMapper.getTypeFactory()
                                    .constructMapType(Map.class, String.class, Object.class)
                    );
                    UUID workId = UUID.fromString((String) payloadMap.get("workId"));

                    WorkDto workDto = workSubmissionService.getWorkById(workId).orElse(null);
                    if (workDto != null) {
                        analysisServiceClient.triggerAnalysis(workDto);
                        log.info("Analysis triggered for work {} via Feign", workId);
                    } else {
                        log.warn("Work {} not found when publishing outbox event", workId);
                    }
                }

                event.setPublished(true);
                outboxEventRepository.save(event);
                log.info("Outbox event {} published successfully", event.getId());
            } catch (Exception e) {
                log.error("Failed to publish outbox event {}: {}", event.getId(), e.getMessage(), e);
            }
        }
    }
}
