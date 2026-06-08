package ru.bauman.scheduler.scheduler;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import ru.bauman.scheduler.config.MissionSchedulerProperties;
import ru.bauman.scheduler.service.MissionOutboxService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConfiguredMissionScheduler {

    private final MissionSchedulerProperties properties;
    private final TaskScheduler taskScheduler;
    private final MissionOutboxService outboxService;

    @PostConstruct
    public void scheduleMissions() {
        log.info("Планирование миссий из конфигурации");
        for (var cfg : properties.missions()) {
            if (!cfg.isValid()) {
                log.warn("Пропущена невалидная миссия: {}", cfg);
                continue;
            }

            Runnable task = () -> {
                // Уникальный идентификатор запуска (секунда/миллисекунда)
                String timeId = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
                log.info("Триггер сработал");
                outboxService.saveMissionCommand(cfg, timeId);
            };

            CronTrigger trigger = new CronTrigger(cfg.cron());
            taskScheduler.schedule(task, trigger);

            log.info("Запланирована миссия: {} | группировка={} | спутник={} | cron={}",
                    cfg.targetType(), cfg.constellationName(), cfg.satelliteName(), cfg.cron());
        }
        log.info("Всего запланировано миссий: {}", properties.missions().size());
    }
}