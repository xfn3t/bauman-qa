package ru.bauman.seminar.constellation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bauman.seminar.constellation.controller.dto.response.ConstellationStatusDto;
import ru.bauman.seminar.constellation.entity.Constellation;
import ru.bauman.seminar.satellite.mapper.SatelliteStatusMapper;

@Mapper(componentModel = "spring", uses = SatelliteStatusMapper.class)
public interface ConstellationStatusMapper {

	@Mapping(target = "satellitesCount", expression = "java(constellation.getSatellites().size())")
	ConstellationStatusDto toDto(Constellation constellation);
}