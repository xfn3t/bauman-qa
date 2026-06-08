package ru.bauman.seminar.constellation.service;

import ru.bauman.seminar.satellite.controller.dto.response.SatelliteResponse;

import java.util.List;

public interface ConstellationMissions {
	List<SatelliteResponse> activateAllSatellites(Long constellationId);
	List<SatelliteResponse> executeAllMissions(Long constellationId);
}