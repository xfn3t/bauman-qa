package ru.bauman.seminar.satellite.creator.impl;

import org.springframework.stereotype.Component;
import ru.bauman.seminar.satellite.creator.SatelliteFactory;
import ru.bauman.seminar.satellite.creator.param.CommunicationSatelliteParam;
import ru.bauman.seminar.satellite.creator.param.SatelliteParam;
import ru.bauman.seminar.satellite.entity.EnergySystem;
import ru.bauman.seminar.satellite.entity.Satellite;
import ru.bauman.seminar.satellite.entity.SatelliteState;
import ru.bauman.seminar.satellite.entity.SatelliteType;
import ru.bauman.seminar.satellite.entity.ext.CommunicationSatellite;
import ru.bauman.seminar.satellite.exception.SpaceOperationException;

@Component
public class CommunicationSatelliteFactory implements SatelliteFactory {

	@Override
	public boolean isSatelliteTypeSupported(SatelliteType type) {
		return SatelliteType.COMMUNICATION == type;
	}

	@Override
	public Satellite createSatelliteWithParameter(SatelliteParam param) {
		if (!(param instanceof CommunicationSatelliteParam p)) {
			throw new SpaceOperationException(
					"CommunicationSatelliteFactory ожидает CommunicationSatelliteParam, получен: "
							+ param.getClass().getSimpleName());
		}
		return CommunicationSatellite.builder()
				.name(p.getName())
				.energySystem(EnergySystem.builder().batteryLevel(p.getBatteryLevel()).build())
				.state(SatelliteState.INACTIVE)
				.bandwidth(p.getBandwidth())
				.build();
	}
}
