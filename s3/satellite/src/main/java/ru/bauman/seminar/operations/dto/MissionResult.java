package ru.bauman.seminar.operations.dto;

import lombok.Builder;
import ru.bauman.seminar.satellite.controller.dto.response.SatelliteResponse;

import java.util.List;

@Builder
public record MissionResult(
		String constellationName,
		MissionType missionType,
		List<SatelliteResponse> processedSatellites,
		int successCount,
		int skippedCount
) {}