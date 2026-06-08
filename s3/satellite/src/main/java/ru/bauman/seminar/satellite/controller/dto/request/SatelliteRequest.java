package ru.bauman.seminar.satellite.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import ru.bauman.seminar.satellite.entity.SatelliteType;
import java.math.BigDecimal;

public record SatelliteRequest(
		@NotBlank(message = "Название спутника обязательно")
		@Schema(description = "Название спутника", example = "Sputnik-1")
		String name,

		@NotNull(message = "Уровень заряда обязателен")
		@DecimalMin(value = "0.0", message = "Уровень заряда должен быть не менее 0")
		@DecimalMax(value = "1.0", message = "Уровень заряда должен быть не более 1")
		@Schema(description = "Уровень заряда батареи (от 0.0 до 1.0)", example = "0.85")
		BigDecimal batteryLevel,

		@NotNull(message = "Тип спутника обязателен")
		@Schema(description = "Тип спутника (COMMUNICATION или IMAGING)")
		SatelliteType type,

		@Positive(message = "Пропускная способность должна быть положительной")
		@Schema(description = "Пропускная способность (для типа COMMUNICATION)", example = "100.0")
		BigDecimal bandwidth,

		@Positive(message = "Разрешение должно быть положительным")
		@Schema(description = "Разрешение снимков (для типа IMAGING)", example = "0.5")
		BigDecimal resolution
) {}