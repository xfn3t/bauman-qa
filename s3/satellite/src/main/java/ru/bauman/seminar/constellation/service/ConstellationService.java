package ru.bauman.seminar.constellation.service;

import ru.bauman.seminar.common.service.CrudService;
import ru.bauman.seminar.common.service.FindByName;
import ru.bauman.seminar.constellation.controller.dto.request.ConstellationRequest;
import ru.bauman.seminar.constellation.controller.dto.response.ConstellationResponse;
import ru.bauman.seminar.constellation.controller.dto.response.ConstellationStatusDto;
import ru.bauman.seminar.satellite.controller.dto.request.SatelliteRequest;
import ru.bauman.seminar.satellite.controller.dto.response.SatelliteResponse;

import java.util.List;

public interface ConstellationService extends
		CrudService<ConstellationRequest, ConstellationResponse, Long>,
		FindByName<ConstellationResponse>,
		ConstellationMissions,
		ConstellationStatusProvider {

	ConstellationResponse addSatellite(Long constellationId, SatelliteRequest request);
	ConstellationResponse removeSatellite(Long constellationId, Long satelliteId);
	List<SatelliteResponse> getSatellites(Long constellationId);

	ConstellationStatusDto getConstellationStatus(Long id);
}