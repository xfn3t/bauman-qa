package ru.bauman.scheduler.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.bauman.scheduler.client.SpaceOperationClient;
import ru.bauman.scheduler.dto.MissionRequest;
import ru.bauman.scheduler.dto.MissionType;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxProcessor {

    private final OutboxRepository outboxRepository;
    private final SpaceOperationClient spaceOperationClient;

    @Scheduled(fixedDelay = 5000) // каждые 5 секунд
    @Transactional
    public void processPendingMessages() {
        var messages = outboxRepository.findPendingMessages();
        if (messages.isEmpty()) return;

        log.info("Обработка {} отложенных миссий", messages.size());

        for (MissionOutbox msg : messages) {
            try {
                MissionRequest request = new MissionRequest(
                        msg.getConstellationName(),
                        msg.getSatelliteName(),
                        MissionType.ALL,
                        msg.isActivateBeforeMission()
                );

                var response = spaceOperationClient.executeMission(request);
                if (response.getStatusCode().is2xxSuccessful()) {
                    msg.setStatus(OutboxStatus.SENT);
                    msg.setProcessedAt(LocalDateTime.now());
                    outboxRepository.save(msg);
                    log.info("Outbox команда выполнена: {}", msg.getDeduplicationId());
                } else {
                    throw new RuntimeException("Основной сервис вернул ошибку: " + response.getStatusCode());
                }
            } catch (Exception e) {
                msg.setAttempts(msg.getAttempts() + 1);
                msg.setLastError(e.getMessage());
                // экспоненциальная задержка: 2^attempts секунд, максимум 1 час
                long delaySec = Math.min(3600, (long) Math.pow(2, msg.getAttempts()));
                msg.setNextRetryAt(LocalDateTime.now().plusSeconds(delaySec));
                outboxRepository.save(msg);
                log.warn("Ошибка отправки (попытка {}): {}. Следующая через {} сек",
                        msg.getAttempts(), e.getMessage(), delaySec);
            }
        }
    }
}