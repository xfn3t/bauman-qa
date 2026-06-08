package ru.bauman.scheduler.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bauman.scheduler.config.MissionSchedulerProperties.MissionConfig;
import ru.bauman.scheduler.dto.MissionRequest;
import ru.bauman.scheduler.dto.MissionType;
import ru.bauman.scheduler.outbox.MissionOutbox;
import ru.bauman.scheduler.outbox.OutboxRepository;
import ru.bauman.scheduler.outbox.OutboxStatus;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MissionOutboxService {

    private final OutboxRepository outboxRepository;

    @Transactional
    public void saveMissionCommand(MissionConfig config, String scheduledTimeId) {

        String deduplicationId = String.format("%s_%s_%s_%s",
                config.targetType(),
                config.constellationName(),
                config.satelliteName() != null ? config.satelliteName() : "",
                scheduledTimeId);

        MissionRequest request = new MissionRequest(
                config.constellationName(),
                config.satelliteName(),
                MissionType.ALL,
                true
        );

        MissionOutbox outbox = MissionOutbox.builder()
                .deduplicationId(deduplicationId)
                .constellationName(request.constellationName())
                .satelliteName(request.satelliteName())
                .missionType(config.targetType().name())
                .activateBeforeMission(request.activateBeforeMission())
                .status(OutboxStatus.PENDING)
                .attempts(0)
                .nextRetryAt(LocalDateTime.now())
                .build();

        outboxRepository.save(outbox);
        log.info("Сохранена команда в outbox: dedupId={}, миссия={}", deduplicationId, config);
    }
}