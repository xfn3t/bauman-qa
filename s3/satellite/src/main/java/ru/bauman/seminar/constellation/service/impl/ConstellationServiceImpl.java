package ru.bauman.seminar.constellation.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bauman.seminar.common.aop.MeasureExecutionTime;
import ru.bauman.seminar.common.exception.EntityNotFoundException;
import ru.bauman.seminar.constellation.controller.dto.request.ConstellationRequest;
import ru.bauman.seminar.constellation.controller.dto.response.ConstellationResponse;
import ru.bauman.seminar.constellation.controller.dto.response.ConstellationStatusDto;
import ru.bauman.seminar.constellation.creator.ConstellationFactory;
import ru.bauman.seminar.constellation.entity.Constellation;
import ru.bauman.seminar.constellation.mapper.ConstellationMapper;
import ru.bauman.seminar.constellation.mapper.ConstellationStatusMapper;
import ru.bauman.seminar.constellation.service.entity.ConstellationEntityService;
import ru.bauman.seminar.constellation.service.ConstellationService;
import ru.bauman.seminar.satellite.controller.dto.request.SatelliteRequest;
import ru.bauman.seminar.satellite.controller.dto.response.SatelliteResponse;
import ru.bauman.seminar.satellite.entity.Satellite;
import ru.bauman.seminar.satellite.service.SatelliteService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConstellationServiceImpl implements ConstellationService {

	private final ConstellationEntityService constellationEntityService;
	private final ConstellationFactory constellationFactory;
	private final SatelliteService satelliteService;
	private final ConstellationMapper constellationMapper;
	private final ConstellationStatusMapper constellationStatusMapper;

	@Override
	@Transactional(readOnly = true)
	@MeasureExecutionTime
	public List<ConstellationResponse> findAll() {
		List<Constellation> constellations = constellationEntityService.findAllWithSatellites();
		return constellationMapper.toResponseList(constellations);
	}

	@Override
	@Transactional(readOnly = true)
	public ConstellationResponse findById(Long id) {
		Constellation constellation = constellationEntityService.findByIdWithSatellites(id);
		return constellationMapper.toResponse(constellation);
	}

	@Override
	@Transactional(readOnly = true)
	public ConstellationResponse findByName(String name) {
		Constellation constellation = constellationEntityService.findByName(name);
		return constellationMapper.toResponse(constellation);
	}

	@Override
	@Transactional
	public ConstellationResponse create(ConstellationRequest request) {
		Constellation constellation = constellationFactory.createConstellation(request.name(), request.description());
		Constellation saved = constellationEntityService.save(constellation);
		return constellationMapper.toResponse(saved);
	}

	@Override
	@Transactional
	public ConstellationResponse update(Long id, ConstellationRequest request) {

		Constellation existing = constellationEntityService.findById(id);
		existing.setName(request.name());
		existing.setDescription(request.description());

		Constellation updated = constellationEntityService.save(existing);
		log.info("Обновлена группировка: {}", updated.getName());
		return constellationMapper.toResponse(updated);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		constellationEntityService.delete(id);
		log.info("Удалена группировка с id: {}", id);
	}

	@Override
	@Transactional
	public ConstellationResponse addSatellite(Long constellationId, SatelliteRequest request) {
		Constellation constellation = constellationEntityService.findByIdWithSatellites(constellationId);

		Satellite satellite = satelliteService.createEntity(request);
		satellite.setConstellation(constellation);
		constellation.getSatellites().add(satellite);

		constellation = constellationEntityService.save(constellation);

		log.info("Добавлен спутник {} в группировку {}", satellite.getName(), constellation.getName());
		return constellationMapper.toResponse(constellation);
	}

	@Override
	@Transactional
	public ConstellationResponse removeSatellite(Long constellationId, Long satelliteId) {
		Constellation constellation = constellationEntityService.findByIdWithSatellites(constellationId);

		Satellite satellite = constellation.getSatellites().stream()
				.filter(s -> s.getId().equals(satelliteId))
				.findFirst()
				.orElseThrow(() -> new EntityNotFoundException("Спутник с id " + satelliteId + " не найден в группировке"));

		satellite.setConstellation(null);
		constellation.getSatellites().remove(satellite);
		constellation = constellationEntityService.save(constellation);

		log.info("Удален спутник {} из группировки {}", satellite.getName(), constellation.getName());
		return constellationMapper.toResponse(constellation);
	}

	@Override
	@Transactional
	@MeasureExecutionTime(operationName = "Активация всех спутников группировки")
	public List<SatelliteResponse> activateAllSatellites(Long constellationId) {
		Constellation constellation = constellationEntityService.findByIdWithSatellites(constellationId);
		log.info("Активация всех спутников в группировке: {}", constellation.getName());

		return constellation.getSatellites().stream()
				.map(satellite -> satelliteService.activate(satellite.getId()))
				.toList();
	}

	@Override
	@Transactional
	public List<SatelliteResponse> executeAllMissions(Long constellationId) {
		Constellation constellation = constellationEntityService.findByIdWithSatellites(constellationId);
		log.info("Выполнение всех миссий в группировке: {}", constellation.getName());

		return constellation.getSatellites().stream()
				.map(satellite -> satelliteService.performMission(satellite.getId()))
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<SatelliteResponse> getSatellites(Long constellationId) {
		return satelliteService.findByConstellationId(constellationId);
	}

	@Override
	@Transactional(readOnly = true)
	public ConstellationStatusDto getConstellationStatus(String constellationName) {
		Constellation constellation = constellationEntityService.findByName(constellationName);
		return constellationStatusMapper.toDto(constellation);
	}

	@Override
	@Transactional(readOnly = true)
	public ConstellationStatusDto getConstellationStatus(Long id) {
		Constellation constellation = constellationEntityService.findByIdWithSatellites(id);
		return constellationStatusMapper.toDto(constellation);
	}
}