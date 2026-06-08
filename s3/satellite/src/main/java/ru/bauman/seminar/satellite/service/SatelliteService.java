package ru.bauman.seminar.satellite.service;

import ru.bauman.seminar.common.service.CrudService;
import ru.bauman.seminar.common.service.FindByName;
import ru.bauman.seminar.satellite.controller.dto.request.SatelliteRequest;
import ru.bauman.seminar.satellite.controller.dto.response.SatelliteResponse;
import ru.bauman.seminar.satellite.entity.Satellite;

import java.util.List;

public interface SatelliteService extends
		CrudService<SatelliteRequest, SatelliteResponse, Long>,
		FindByName<SatelliteResponse> {

	SatelliteResponse activate(Long id);
	SatelliteResponse deactivate(Long id);
	SatelliteResponse performMission(Long id);
	List<SatelliteResponse> findByConstellationId(Long constellationId);
	Satellite createEntity(SatelliteRequest request);
}