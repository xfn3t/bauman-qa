package ru.bauman.telemetry;

import io.grpc.Context;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.kafka.core.KafkaTemplate;
import ru.bauman.telemetry.proto.TelemetryRequest;
import ru.bauman.telemetry.proto.TelemetryServiceGrpc;
import ru.bauman.telemetry.proto.TelemetryUpdate;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class TelemetryServiceImpl
    extends TelemetryServiceGrpc.TelemetryServiceImplBase
{

    private static final String TOPIC = "telemetry";

    private static final List<String> SATELLITES = List.of(
        "Связь-1",
        "Связь-2",
        "ДЗЗ-1",
        "ДЗЗ-2",
        "ДЗЗ-3"
    );

    private static final double INTERNAL_TEMP_BASE = 22.0;
    private static final double INTERNAL_TEMP_AMPLITUDE = 5.0;
    private static final double EXTERNAL_TEMP_MIN = -150.0;
    private static final double EXTERNAL_TEMP_MAX = 120.0;
    private static final int INTERVAL_SEC = 2;

    private final Random random = new Random();
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    private ScheduledExecutorService kafkaScheduler;

    @PostConstruct
    void startKafkaProducer() {
        kafkaScheduler = Executors.newSingleThreadScheduledExecutor();
        kafkaScheduler.scheduleAtFixedRate(
            this::sendToKafka,
            0,
            INTERVAL_SEC,
            TimeUnit.SECONDS
        );
        log.info("Kafka-продюсер запущен, топик: {}", TOPIC);
    }

    @PreDestroy
    void stopKafkaProducer() {
        if (kafkaScheduler != null) {
            kafkaScheduler.shutdown();
        }
    }

    private void sendToKafka() {
        try {
            long now = System.currentTimeMillis();
            for (String name : SATELLITES) {
                double internal = generateInternalTemp();
                double external = generateExternalTemp();

                TelemetryUpdate update = TelemetryUpdate.newBuilder()
                    .setSatelliteName(name)
                    .setInternalTemperature(internal)
                    .setExternalTemperature(external)
                    .setTimestamp(now)
                    .build();

                kafkaTemplate.send(TOPIC, name, update.toByteArray());
                log.info(
                    "{}: внутр. {}°C / внеш. {}°C",
                    name,
                    internal,
                    external
                );
            }
        } catch (Exception e) {
            log.error("Ошибка отправки в Kafka: {}", e.getMessage(), e);
        }
    }


    @Override
    public void streamTelemetry(
        TelemetryRequest request,
        StreamObserver<TelemetryUpdate> responseObserver
    ) {
        String filter = request.getSatelliteName();
        log.info(
            "gRPC клиент подключен, фильтр: '{}'",
            filter.isEmpty() ? "ВСЕ" : filter
        );

        List<String> targets = filter.isEmpty()
            ? SATELLITES
            : SATELLITES.stream()
                  .filter(s -> s.equalsIgnoreCase(filter))
                  .toList();

        if (targets.isEmpty()) {
            log.warn("Спутник '{}' не найден", filter);
            responseObserver.onCompleted();
            return;
        }

        ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture<?> task = scheduler.scheduleAtFixedRate(
            () -> {
                try {
                    long now = System.currentTimeMillis();
                    for (String name : targets) {
                        responseObserver.onNext(
                            TelemetryUpdate.newBuilder()
                                .setSatelliteName(name)
                                .setInternalTemperature(generateInternalTemp())
                                .setExternalTemperature(generateExternalTemp())
                                .setTimestamp(now)
                                .build()
                        );
                    }
                } catch (Exception e) {
                    log.error("Ошибка gRPC стрима: {}", e.getMessage(), e);
                    responseObserver.onError(e);
                }
            },
            0,
            INTERVAL_SEC,
            TimeUnit.SECONDS
        );

        Context.current().addListener(ctx -> {
            log.info("gRPC клиент отключился");
            task.cancel(true);
            scheduler.shutdown();
        }, scheduler);
    }

    private double generateInternalTemp() {
        double raw =
            INTERNAL_TEMP_BASE +
            random.nextGaussian() * INTERNAL_TEMP_AMPLITUDE;
        return Math.round(raw * 100.0) / 100.0;
    }

    private double generateExternalTemp() {
        double range = EXTERNAL_TEMP_MAX - EXTERNAL_TEMP_MIN;
        double raw = EXTERNAL_TEMP_MIN + random.nextDouble() * range;
        return Math.round(raw * 100.0) / 100.0;
    }
}
