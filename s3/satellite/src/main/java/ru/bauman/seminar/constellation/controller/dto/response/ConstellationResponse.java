package ru.bauman.seminar.constellation.controller.dto.response;

import ru.bauman.seminar.satellite.controller.dto.response.SatelliteResponse;

import java.util.List;

public record ConstellationResponse(
		Long id,
		String name,
		String description,
		List<SatelliteResponse> satellites
) {}