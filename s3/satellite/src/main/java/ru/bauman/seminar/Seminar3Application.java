package ru.bauman.seminar;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.bauman.seminar.constellation.service.ConstellationService;
import ru.bauman.seminar.operations.SpaceOperationCenterService;
import ru.bauman.seminar.operations.dto.*;
import ru.bauman.seminar.satellite.controller.dto.request.SatelliteRequest;
import ru.bauman.seminar.satellite.entity.SatelliteType;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@SpringBootApplication
public class Seminar3Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(Seminar3Application.class, args);

		ConstellationService constellationService = context.getBean(ConstellationService.class);
		SpaceOperationCenterService operationCenter = context.getBean(SpaceOperationCenterService.class);

		log.info("\n🔄 Очистка базы данных...");
		constellationService.findAll().forEach(c -> constellationService.delete(c.id()));
		log.info("✅ База данных очищена.\n");

		log.info("СОЗДАНИЕ СПЕЦИАЛИЗИРОВАННЫХ СПУТНИКОВ:");
		log.info("---------------------------------------------");

		operationCenter.addSatellites(AddSatelliteRequest.builder()
				.constellationName("Орбита-1")
				.constellationDescription("Основная группировка связи")
				.satellites(List.of(
						new SatelliteRequest("Связь-1", BigDecimal.valueOf(0.85), SatelliteType.COMMUNICATION, BigDecimal.valueOf(500.0), null),
						new SatelliteRequest("ДЗЗ-1",   BigDecimal.valueOf(0.92), SatelliteType.IMAGING,        null, BigDecimal.valueOf(2.5)),
						new SatelliteRequest("ДЗЗ-2",   BigDecimal.valueOf(0.45), SatelliteType.IMAGING,        null, BigDecimal.valueOf(1.0))
				))
				.build());
		log.info("Создана спутниковая группировка: Орбита-1");

		operationCenter.addSatellites(AddSatelliteRequest.builder()
				.constellationName("Орбита-2")
				.constellationDescription("Резервная группировка")
				.satellites(List.of(
						new SatelliteRequest("Связь-2", BigDecimal.valueOf(0.75), SatelliteType.COMMUNICATION, BigDecimal.valueOf(1000.0), null),
						new SatelliteRequest("ДЗЗ-3",   BigDecimal.valueOf(0.15), SatelliteType.IMAGING,        null, BigDecimal.valueOf(0.5))
				))
				.build());
		log.info("Создана спутниковая группировка: Орбита-2");
		log.info("---------------------------------------------\n");

		log.info("=== АКТИВАЦИЯ СПУТНИКОВ В ГРУППИРОВКЕ: Орбита-1 ===");
		MissionResult orbit1Result = operationCenter.activateAndExecuteAll("Орбита-1");
		orbit1Result.processedSatellites().forEach(sat -> log.info("✅ {}: Активация успешна", sat.name()));
		log.info("");

		log.info("=== ВЫПОЛНЕНИЕ МИССИЙ ДЛЯ ГРУППИРОВКИ: Орбита-1 ===");
		log.info("ВЫПОЛНЕНИЕ МИССИЙ ГРУППИРОВКИ ОРБИТА-1");
		log.info("==================================================");
		log.info("Выполнено: {}, пропущено: {}", orbit1Result.successCount(), orbit1Result.skippedCount());
		log.info("");

		log.info("=== СТАТУС ГРУППИРОВКИ: Орбита-1 ===");
		SystemStatusDto systemStatus = operationCenter.getSystemStatus();
		systemStatus.constellationStatuses().stream()
				.filter(s -> s.getName().equals("Орбита-1"))
				.findFirst()
				.ifPresent(s -> log.info(s.getName()));

		log.info("=== ВСЕ ГРУППИРОВКИ (СОДЕРЖИМОЕ РЕПОЗИТОРИЯ) ===");
		constellationService.findAll().forEach(c -> {
			log.info("Группировка: {} (id={})", c.name(), c.id());
			log.info("  Описание: {}", c.description());
			log.info("  Спутники:");
			c.satellites().forEach(s -> {
				StringBuilder sb = new StringBuilder();
				sb.append(String.format("    - %s (id=%d, тип=%s, заряд=%.0f%%, состояние=%s",
						s.name(), s.id(), s.type(),
						s.batteryLevel().multiply(BigDecimal.valueOf(100)),
						s.state()));
				if (s.bandwidth() != null) {
					sb.append(String.format(", пропускная способность=%.1f Мбит/с", s.bandwidth()));
				}
				if (s.resolution() != null) {
					sb.append(String.format(", разрешение=%.1f м/пиксель, снимков=%d",
							s.resolution(), s.photosTaken()));
				}
				log.info(sb.toString());
			});
			log.info("");
		});
	}
}