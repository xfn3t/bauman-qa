package ru.bauman.seminar.operations.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import ru.bauman.seminar.satellite.controller.dto.request.SatelliteRequest;

import java.util.List;

@Builder
public record AddSatelliteRequest(
		@NotBlank(message = "Название группировки обязательно")
		String constellationName,

		String constellationDescription,

		@NotEmpty(message = "Необходимо указать хотя бы один спутник")
		List<@Valid SatelliteRequest> satellites
) {}