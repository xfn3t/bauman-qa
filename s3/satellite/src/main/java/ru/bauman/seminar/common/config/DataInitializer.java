package ru.bauman.seminar.common.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.bauman.seminar.constellation.controller.dto.request.ConstellationRequest;
import ru.bauman.seminar.constellation.service.ConstellationService;
import ru.bauman.seminar.satellite.controller.dto.request.SatelliteRequest;
import ru.bauman.seminar.satellite.entity.SatelliteType;

import java.math.BigDecimal;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class DataInitializer {

	private final ConstellationService constellationService;

	@PostConstruct
	@Transactional
	public void init() {
		if (!constellationService.findAll().isEmpty()) {
			log.info("Данные уже существуют, пропускаем инициализацию");
			return;
		}

		log.info("Инициализация тестовых данных...");
		try {
			var orbit1 = constellationService.create(
					new ConstellationRequest("Орбита-1", "Основная группировка связи")
			);
			var orbit2 = constellationService.create(
					new ConstellationRequest("Орбита-2", "Резервная группировка")
			);

			constellationService.addSatellite(orbit1.id(),
					new SatelliteRequest("Связь-1", BigDecimal.valueOf(0.85), SatelliteType.COMMUNICATION,
							BigDecimal.valueOf(500.0), null));
			constellationService.addSatellite(orbit1.id(),
					new SatelliteRequest("ДЗЗ-1", BigDecimal.valueOf(0.92), SatelliteType.IMAGING,
							null, BigDecimal.valueOf(2.5)));
			constellationService.addSatellite(orbit1.id(),
					new SatelliteRequest("ДЗЗ-2", BigDecimal.valueOf(0.45), SatelliteType.IMAGING,
							null, BigDecimal.valueOf(1.0)));
			constellationService.addSatellite(orbit2.id(),
					new SatelliteRequest("Связь-2", BigDecimal.valueOf(0.75), SatelliteType.COMMUNICATION,
							BigDecimal.valueOf(1000.0), null));
			constellationService.addSatellite(orbit2.id(),
					new SatelliteRequest("ДЗЗ-3", BigDecimal.valueOf(0.15), SatelliteType.IMAGING,
							null, BigDecimal.valueOf(0.5)));

			log.info("Тестовые данные успешно инициализированы");
		} catch (Exception e) {
			log.error("Ошибка при инициализации тестовых данных", e);
		}
	}
}