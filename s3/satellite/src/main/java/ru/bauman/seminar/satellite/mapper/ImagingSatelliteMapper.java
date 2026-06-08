package ru.bauman.seminar.satellite.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bauman.seminar.satellite.controller.dto.response.SatelliteStatusDto;
import ru.bauman.seminar.satellite.entity.ext.ImagingSatellite;

@Mapper(componentModel = "spring")
public interface ImagingSatelliteMapper {

	@Mapping(source = "resolution", target = "resolution")
	@Mapping(source = "photosTaken", target = "photosTaken")
	@Mapping(target = "bandwidth", ignore = true)
	SatelliteStatusDto toDto(ImagingSatellite satellite);
}