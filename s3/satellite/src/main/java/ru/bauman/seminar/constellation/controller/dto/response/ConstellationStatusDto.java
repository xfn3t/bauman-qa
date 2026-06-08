package ru.bauman.seminar.constellation.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import ru.bauman.seminar.satellite.controller.dto.response.SatelliteStatusDto;

import java.util.List;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConstellationStatusDto {
	Long id;
	String name;
	String description;
	int satellitesCount;
	List<SatelliteStatusDto> satellites;
}