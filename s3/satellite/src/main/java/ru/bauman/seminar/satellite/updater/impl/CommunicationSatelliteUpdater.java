package ru.bauman.seminar.satellite.updater.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.bauman.seminar.satellite.controller.dto.request.SatelliteRequest;
import ru.bauman.seminar.satellite.entity.Satellite;
import ru.bauman.seminar.satellite.entity.SatelliteType;
import ru.bauman.seminar.satellite.entity.ext.CommunicationSatellite;
import ru.bauman.seminar.satellite.updater.SatelliteUpdater;

@Component
@RequiredArgsConstructor
public class CommunicationSatelliteUpdater implements SatelliteUpdater {

	@Override
	public SatelliteType getType() {
		return SatelliteType.COMMUNICATION;
	}

	@Override
	public void update(Satellite satellite, SatelliteRequest request) {
		CommunicationSatellite comm = (CommunicationSatellite) satellite;
		comm.setBandwidth(request.bandwidth());
	}
}