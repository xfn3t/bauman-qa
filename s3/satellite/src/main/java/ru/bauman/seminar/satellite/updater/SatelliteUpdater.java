package ru.bauman.seminar.satellite.updater;

import ru.bauman.seminar.satellite.controller.dto.request.SatelliteRequest;
import ru.bauman.seminar.satellite.entity.Satellite;
import ru.bauman.seminar.satellite.entity.SatelliteType;

public interface SatelliteUpdater {
	SatelliteType getType();
	void update(Satellite satellite, SatelliteRequest request);
}