package ru.bauman.seminar.satellite.creator.impl;

import org.springframework.stereotype.Component;
import ru.bauman.seminar.satellite.creator.SatelliteFactory;
import ru.bauman.seminar.satellite.creator.param.ImagingSatelliteParam;
import ru.bauman.seminar.satellite.creator.param.SatelliteParam;
import ru.bauman.seminar.satellite.entity.EnergySystem;
import ru.bauman.seminar.satellite.entity.Satellite;
import ru.bauman.seminar.satellite.entity.SatelliteState;
import ru.bauman.seminar.satellite.entity.SatelliteType;
import ru.bauman.seminar.satellite.entity.ext.ImagingSatellite;
import ru.bauman.seminar.satellite.exception.SpaceOperationException;

@Component
public class ImagingSatelliteFactory implements SatelliteFactory {

	@Override
	public boolean isSatelliteTypeSupported(SatelliteType type) {
		return SatelliteType.IMAGING == type;
	}

	@Override
	public Satellite createSatelliteWithParameter(SatelliteParam param) {
		if (!(param instanceof ImagingSatelliteParam p)) {
			throw new SpaceOperationException(
					"ImagingSatelliteFactory ожидает ImagingSatelliteParam, получен: "
							+ param.getClass().getSimpleName());
		}
		return ImagingSatellite.builder()
				.name(p.getName())
				.energySystem(EnergySystem.builder().batteryLevel(p.getBatteryLevel()).build())
				.state(SatelliteState.INACTIVE)
				.resolution(p.getResolution())
				.photosTaken(0)
				.build();
	}
}

