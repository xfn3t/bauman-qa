package ru.bauman.seminar.satellite.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bauman.seminar.satellite.controller.dto.request.SatelliteRequest;
import ru.bauman.seminar.satellite.controller.dto.response.SatelliteResponse;
import ru.bauman.seminar.satellite.creator.SatelliteCreationService;
import ru.bauman.seminar.satellite.creator.param.CommunicationSatelliteParam;
import ru.bauman.seminar.satellite.creator.param.ImagingSatelliteParam;
import ru.bauman.seminar.satellite.creator.param.SatelliteParam;
import ru.bauman.seminar.satellite.entity.EnergySystem;
import ru.bauman.seminar.satellite.entity.Satellite;
import ru.bauman.seminar.satellite.entity.SatelliteState;
import ru.bauman.seminar.satellite.entity.SatelliteType;
import ru.bauman.seminar.satellite.mapper.SatelliteMapper;
import ru.bauman.seminar.satellite.service.SatelliteService;
import ru.bauman.seminar.satellite.service.entity.SatelliteEntityService;
import ru.bauman.seminar.satellite.updater.SatelliteUpdater;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SatelliteServiceImpl implements SatelliteService {

	private final SatelliteEntityService satelliteEntityService;
	private final SatelliteCreationService creationService; // ← заменили SatelliteFactory
	private final Map<SatelliteType, SatelliteUpdater> updaters;
	private final SatelliteMapper mapper;

	public SatelliteServiceImpl(
			SatelliteEntityService satelliteEntityService,
			SatelliteCreationService creationService,
			List<SatelliteUpdater> updaterList,
			SatelliteMapper mapper) {
		this.satelliteEntityService = satelliteEntityService;
		this.creationService = creationService;
		this.updaters = updaterList.stream()
				.collect(Collectors.toMap(SatelliteUpdater::getType, Function.identity()));
		this.mapper = mapper;
	}


	@Override
	@Transactional(readOnly = true)
	public List<SatelliteResponse> findAll() {
		return satelliteEntityService.findAll().stream().map(mapper::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public SatelliteResponse findById(Long id) {
		return mapper.toResponse(satelliteEntityService.findById(id));
	}

	@Override
	@Transactional(readOnly = true)
	public SatelliteResponse findByName(String name) {
		return mapper.toResponse(satelliteEntityService.findByName(name));
	}

	@Override @Transactional
	public SatelliteResponse create(SatelliteRequest request) {
		Satellite satellite = satelliteEntityService.save(createEntity(request));
		log.info("Создан спутник: {} типа {}", satellite.getName(), satellite.getType());
		return mapper.toResponse(satellite);
	}

	@Override @Transactional
	public SatelliteResponse update(Long id, SatelliteRequest request) {

		Satellite satellite = satelliteEntityService.findById(id);

		if (!satellite.getType().equals(request.type())) {
			throw new IllegalArgumentException("Нельзя изменить тип спутника");
		}

		satellite.setName(request.name());

		EnergySystem old = satellite.getEnergySystem();
		satellite.setEnergySystem(EnergySystem.builder()
				.batteryLevel(request.batteryLevel())
				.minBattery(old.getMinBattery())
				.maxBattery(old.getMaxBattery())
				.lowBatteryThreshold(old.getLowBatteryThreshold())
				.build());

		SatelliteUpdater updater = updaters.get(request.type());

		if (updater == null)
			throw new IllegalStateException("Не найден updater для типа " + request.type());

		updater.update(satellite, request);
		satellite = satelliteEntityService.save(satellite);

		log.info("Обновлен спутник: {}", satellite.getName());
		return mapper.toResponse(satellite);
	}

	@Override @Transactional
	public void delete(Long id) {
		satelliteEntityService.delete(id);
	}


	@Override @Transactional
	public SatelliteResponse activate(Long id) {
		Satellite s = satelliteEntityService.findById(id);
		if (s.activate()) {
			s = satelliteEntityService.save(s);
			log.info("✅ {}: Активация успешна", s.getName());
		} else {
			log.warn("⚠️ {}: Не удалось активировать (состояние: {}, заряд: {}%)",
					s.getName(), s.getState(),
					s.getBatteryLevel().multiply(BigDecimal.valueOf(100)).intValue());
		}
		return mapper.toResponse(s);
	}

	@Override @Transactional
	public SatelliteResponse deactivate(Long id) {
		Satellite s = satelliteEntityService.findById(id);
		if (s.deactivate()) { s = satelliteEntityService.save(s); log.info("⏸️ {}: Деактивирован", s.getName()); }
		return mapper.toResponse(s);
	}

	@Override @Transactional
	public SatelliteResponse performMission(Long id) {
		Satellite s = satelliteEntityService.findById(id);
		s.performMission();
		satelliteEntityService.save(s);
		return mapper.toResponse(s);
	}

	@Override @Transactional(readOnly = true)
	public List<SatelliteResponse> findByConstellationId(Long constellationId) {
		return satelliteEntityService.findByConstellationId(constellationId).stream()
				.map(mapper::toResponse).toList();
	}

	@Override
	public Satellite createEntity(SatelliteRequest request) {
		return creationService.createSatellite(toParam(request));
	}

	private static SatelliteParam toParam(SatelliteRequest r) {
		return switch (r.type()) {
			case COMMUNICATION -> new CommunicationSatelliteParam(r.name(), r.batteryLevel(), r.bandwidth());
			case IMAGING       -> new ImagingSatelliteParam(r.name(), r.batteryLevel(), r.resolution());
		};
	}
}
