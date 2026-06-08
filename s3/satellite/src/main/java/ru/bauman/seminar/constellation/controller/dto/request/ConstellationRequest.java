package ru.bauman.seminar.constellation.controller.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ConstellationRequest(
		@NotBlank(message = "Название группировки обязательно")
		String name,

		String description
) {}