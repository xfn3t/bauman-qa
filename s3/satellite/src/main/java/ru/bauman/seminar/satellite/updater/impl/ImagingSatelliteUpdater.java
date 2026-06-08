package ru.bauman.seminar.satellite.updater.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.bauman.seminar.satellite.controller.dto.request.SatelliteRequest;
import ru.bauman.seminar.satellite.entity.Satellite;
import ru.bauman.seminar.satellite.entity.SatelliteType;
import ru.bauman.seminar.satellite.entity.ext.ImagingSatellite;
import ru.bauman.seminar.satellite.updater.SatelliteUpdater;

@Component
@RequiredArgsConstructor
public class ImagingSatelliteUpdater implements SatelliteUpdater {

	@Override
	public SatelliteType getType() {
		return SatelliteType.IMAGING;
	}

	@Override
	public void update(Satellite satellite, SatelliteRequest request) {
		ImagingSatellite img = (ImagingSatellite) satellite;
		img.setResolution(request.resolution());
	}
}