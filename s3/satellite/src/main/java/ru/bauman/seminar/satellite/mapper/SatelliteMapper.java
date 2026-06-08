package ru.bauman.seminar.satellite.mapper;

import org.mapstruct.*;
import ru.bauman.seminar.satellite.controller.dto.response.SatelliteResponse;
import ru.bauman.seminar.satellite.entity.ext.CommunicationSatellite;
import ru.bauman.seminar.satellite.entity.ext.ImagingSatellite;
import ru.bauman.seminar.satellite.entity.Satellite;
import ru.bauman.seminar.satellite.entity.SatelliteType;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface SatelliteMapper {

	@Mapping(target = "state", source = "state")
	@Mapping(target = "type", source = "entity", qualifiedByName = "getType")
	@Mapping(target = "bandwidth", source = "entity", qualifiedByName = "getBandwidthIfCommunication")
	@Mapping(target = "resolution", source = "entity", qualifiedByName = "getResolutionIfImaging")
	@Mapping(target = "photosTaken", source = "entity", qualifiedByName = "getPhotosTakenIfImaging")
	SatelliteResponse toResponse(Satellite entity);

	@Named("getType")
	default SatelliteType getType(Satellite entity) {
		return entity.getType();
	}

	@Named("getBandwidthIfCommunication")
	default BigDecimal getBandwidthIfCommunication(Satellite entity) {
		return entity instanceof CommunicationSatellite c ? c.getBandwidth() : null;
	}

	@Named("getResolutionIfImaging")
	default BigDecimal getResolutionIfImaging(Satellite entity) {
		return entity instanceof ImagingSatellite i ? i.getResolution() : null;
	}

	@Named("getPhotosTakenIfImaging")
	default Integer getPhotosTakenIfImaging(Satellite entity) {
		return entity instanceof ImagingSatellite i ? i.getPhotosTaken() : null;
	}
}