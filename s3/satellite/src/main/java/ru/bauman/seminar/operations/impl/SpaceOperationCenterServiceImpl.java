package ru.bauman.seminar.operations.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bauman.seminar.constellation.controller.dto.request.ConstellationRequest;
import ru.bauman.seminar.constellation.controller.dto.response.ConstellationResponse;
import ru.bauman.seminar.constellation.controller.dto.response.ConstellationStatusDto;
import ru.bauman.seminar.constellation.service.ConstellationService;
import ru.bauman.seminar.operations.SpaceOperationCenterService;
import ru.bauman.seminar.operations.dto.*;
import ru.bauman.seminar.satellite.controller.dto.request.SatelliteRequest;
import ru.bauman.seminar.satellite.controller.dto.response.SatelliteResponse;
import ru.bauman.seminar.satellite.entity.SatelliteState;
import ru.bauman.seminar.satellite.entity.SatelliteType;
import ru.bauman.seminar.satellite.service.SatelliteService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpaceOperationCenterServiceImpl implements SpaceOperationCenterService {

	private final ConstellationService constellationService;
	private final SatelliteService satelliteService;

	@Override
	@Transactional
	public ConstellationResponse addSatellites(AddSatelliteRequest request) {
		log.info("Facade: добавление {} спутников в группировку '{}'",
				request.satellites().size(), request.constellationName());

		Long constellationId = findOrCreateConstellation(
				request.constellationName(),
				request.constellationDescription()
		);

		ConstellationResponse response = null;
		for (SatelliteRequest satRequest : request.satellites()) {
			response = constellationService.addSatellite(constellationId, satRequest);
			log.info("  ✅ Добавлен спутник '{}'", satRequest.name());
		}

		return response;
	}

	@Override
	@Transactional
	public MissionResult executeMission(MissionRequest request) {
		log.info("Facade: выполнение миссии {} в группировке '{}' (activate={})",
				request.missionType(), request.constellationName(), request.activateBeforeMission());

		ConstellationResponse constellation = constellationService.findByName(request.constellationName());

		if (request.activateBeforeMission()) {
			constellationService.activateAllSatellites(constellation.id());
			log.info("  ⚡ Спутники активированы");
		}

		List<SatelliteResponse> allSatellites = constellationService.getSatellites(constellation.id());
		List<SatelliteResponse> targets = filterByMissionType(allSatellites, request.missionType());

		List<SatelliteResponse> results = new ArrayList<>();
		int successCount = 0;
		int skippedCount = 0;

		for (SatelliteResponse sat : targets) {
			SatelliteResponse result = satelliteService.performMission(sat.id());
			results.add(result);
			if (result.state() == SatelliteState.ACTIVE || result.state() == SatelliteState.CRITICAL) {
				successCount++;
			} else {
				skippedCount++;
			}
		}

		log.info("  📊 Итог: выполнено={}, пропущено={}", successCount, skippedCount);

		return MissionResult.builder()
				.constellationName(request.constellationName())
				.missionType(request.missionType())
				.processedSatellites(results)
				.successCount(successCount)
				.skippedCount(skippedCount)
				.build();
	}

	@Override
	@Transactional
	public MissionResult activateAndExecuteAll(String constellationName) {
		log.info("Facade: активация и запуск всех миссий в '{}'", constellationName);

		return executeMission(MissionRequest.builder()
				.constellationName(constellationName)
				.missionType(MissionType.ALL)
				.activateBeforeMission(true)
				.build());
	}

	@Override
	@Transactional(readOnly = true)
	public SystemStatusDto getSystemStatus() {
		log.info("Facade: запрос агрегированного статуса системы");

		List<ConstellationResponse> constellations = constellationService.findAll();

		List<ConstellationStatusDto> statuses = constellations.stream()
				.map(c -> constellationService.getConstellationStatus(c.id()))
				.toList();

		int totalSatellites = 0;
		int activeSatellites = 0;
		int criticalSatellites = 0;

		for (ConstellationResponse c : constellations) {
			for (var sat : c.satellites()) {
				totalSatellites++;
				if (sat.state() == SatelliteState.ACTIVE)   activeSatellites++;
				if (sat.state() == SatelliteState.CRITICAL) criticalSatellites++;
			}
		}

		SystemStatusDto status = SystemStatusDto.builder()
				.totalConstellations(constellations.size())
				.totalSatellites(totalSatellites)
				.activeSatellites(activeSatellites)
				.criticalSatellites(criticalSatellites)
				.constellationStatuses(statuses)
				.build();

		log.info("  🌍 Группировок: {}, Спутников: {}, Активных: {}, Критических: {}",
				status.totalConstellations(), status.totalSatellites(),
				status.activeSatellites(), status.criticalSatellites());

		return status;
	}

	private Long findOrCreateConstellation(String name, String description) {
		return constellationService.findAll().stream()
				.filter(c -> c.name().equals(name))
				.findFirst()
				.map(existing -> {
					log.info("  ℹ️ Группировка '{}' уже существует (id={})", name, existing.id());
					return existing.id();
				})
				.orElseGet(() -> {
					ConstellationResponse created = constellationService.create(
							new ConstellationRequest(name, description)
					);
					log.info("  ✨ Создана новая группировка '{}' (id={})", name, created.id());
					return created.id();
				});
	}

	private List<SatelliteResponse> filterByMissionType(
			List<SatelliteResponse> satellites,
			MissionType missionType
	) {
		return switch (missionType) {
			case COMMUNICATION -> satellites.stream()
					.filter(s -> s.type() == SatelliteType.COMMUNICATION)
					.toList();
			case IMAGING -> satellites.stream()
					.filter(s -> s.type() == SatelliteType.IMAGING)
					.toList();
			case ALL -> satellites;
		};
	}
}