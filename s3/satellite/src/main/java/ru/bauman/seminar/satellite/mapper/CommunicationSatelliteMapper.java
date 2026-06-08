package ru.bauman.seminar.satellite.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bauman.seminar.satellite.controller.dto.response.SatelliteStatusDto;
import ru.bauman.seminar.satellite.entity.ext.CommunicationSatellite;

@Mapper(componentModel = "spring")
public interface CommunicationSatelliteMapper {

	@Mapping(source = "bandwidth", target = "bandwidth")
	@Mapping(target = "resolution", ignore = true)
	@Mapping(target = "photosTaken", ignore = true)
	SatelliteStatusDto toDto(CommunicationSatellite satellite);
}