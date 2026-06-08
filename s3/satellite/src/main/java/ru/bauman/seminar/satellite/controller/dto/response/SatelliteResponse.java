package ru.bauman.seminar.satellite.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.bauman.seminar.satellite.entity.SatelliteState;
import ru.bauman.seminar.satellite.entity.SatelliteType;
import java.math.BigDecimal;

public record SatelliteResponse(
		@Schema(description = "Уникальный идентификатор спутника", example = "1")
		Long id,

		@Schema(description = "Название спутника", example = "Sputnik-1")
		String name,

		@Schema(description = "Уровень заряда батареи", example = "0.85")
		BigDecimal batteryLevel,

		@Schema(description = "Состояние спутника")
		SatelliteState state,

		@Schema(description = "Тип спутника")
		SatelliteType type,

		@Schema(description = "Пропускная способность (для COMMUNICATION)", example = "100.0")
		BigDecimal bandwidth,

		@Schema(description = "Разрешение снимков (для IMAGING)", example = "0.5")
		BigDecimal resolution,

		@Schema(description = "Количество сделанных снимков (для IMAGING)", example = "42")
		Integer photosTaken
) {}