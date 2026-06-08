package ru.bauman.seminar.satellite.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;
import ru.bauman.seminar.satellite.entity.SatelliteState;
import ru.bauman.seminar.satellite.entity.SatelliteType;
import java.math.BigDecimal;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SatelliteStatusDto {
	@Schema(description = "Идентификатор спутника", example = "1")
	Long id;

	@Schema(description = "Название спутника", example = "Sputnik-1")
	String name;

	@Schema(description = "Тип спутника")
	SatelliteType type;

	@Schema(description = "Уровень заряда", example = "0.75")
	BigDecimal batteryLevel;

	@Schema(description = "Состояние спутника")
	SatelliteState state;

	@Schema(description = "Пропускная способность (только для COMMUNICATION)", example = "100.0")
	BigDecimal bandwidth;

	@Schema(description = "Разрешение снимков (только для IMAGING)", example = "0.5")
	BigDecimal resolution;

	@Schema(description = "Количество сделанных снимков (только для IMAGING)", example = "10")
	Integer photosTaken;
}