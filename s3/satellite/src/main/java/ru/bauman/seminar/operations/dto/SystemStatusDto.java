package ru.bauman.seminar.operations.dto;

import lombok.Builder;
import ru.bauman.seminar.constellation.controller.dto.response.ConstellationStatusDto;

import java.util.List;

@Builder
public record SystemStatusDto(
		int totalConstellations,
		int totalSatellites,
		int activeSatellites,
		int criticalSatellites,
		List<ConstellationStatusDto> constellationStatuses
) {}