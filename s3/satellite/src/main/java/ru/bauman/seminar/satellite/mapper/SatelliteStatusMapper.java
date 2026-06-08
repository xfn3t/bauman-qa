package ru.bauman.seminar.satellite.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bauman.seminar.satellite.controller.dto.response.SatelliteStatusDto;
import ru.bauman.seminar.satellite.entity.Satellite;

@Mapper(componentModel = "spring", uses = {CommunicationSatelliteMapper.class, ImagingSatelliteMapper.class})
public interface SatelliteStatusMapper {

	@Mapping(target = "state", source = "state")
	@Mapping(target = "bandwidth", ignore = true)
	@Mapping(target = "resolution", ignore = true)
	@Mapping(target = "photosTaken", ignore = true)
	SatelliteStatusDto toDto(Satellite satellite);
}