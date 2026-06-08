package ru.bauman.seminar.telemetry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bauman.seminar.satellite.repository.SatelliteRepository;
import ru.bauman.telemetry.proto.TelemetryUpdate;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaTelemetryConsumer {

    private final SatelliteRepository satelliteRepository;

    @KafkaListener(topics = "telemetry", groupId = "satellite-group")
    @Transactional
    public void consume(byte[] data) {
        try {
            TelemetryUpdate update = TelemetryUpdate.parseFrom(data);

            satelliteRepository
                .findByName(update.getSatelliteName())
                .ifPresent(satellite -> {
                    satellite.setInternalTemperature(
                        update.getInternalTemperature()
                    );
                    satellite.setExternalTemperature(
                        update.getExternalTemperature()
                    );
                    satelliteRepository.save(satellite);
                    log.info(
                        "{} получил телеметрию (Kafka): внутр. {}°C / внеш. {}°C",
                        update.getSatelliteName(),
                        update.getInternalTemperature(),
                        update.getExternalTemperature()
                    );
                });
        } catch (Exception e) {
            log.error("Ошибка обработки Kafka-сообщения: {}", e.getMessage());
        }
    }
}
