package ru.bauman.seminar.operations.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record MissionRequest(
		@NotBlank(message = "Название группировки обязательно")
		String constellationName,

		@NotNull(message = "Тип миссии обязателен")
		MissionType missionType,

		boolean activateBeforeMission
) {}