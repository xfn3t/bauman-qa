package ru.bauman.seminar.constellation.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bauman.seminar.constellation.controller.dto.request.ConstellationRequest;
import ru.bauman.seminar.constellation.controller.dto.response.ConstellationResponse;
import ru.bauman.seminar.constellation.entity.Constellation;

import java.util.List;

@Mapper(
		componentModel = "spring",
		uses = ru.bauman.seminar.satellite.mapper.SatelliteMapper.class,
		builder = @Builder(disableBuilder = true)
)
public interface ConstellationMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "satellites", ignore = true)
	@Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
	@Mapping(target = "updatedAt", ignore = true)
	Constellation toEntity(ConstellationRequest request);

	ConstellationResponse toResponse(Constellation entity);

	List<ConstellationResponse> toResponseList(List<Constellation> entities);
}