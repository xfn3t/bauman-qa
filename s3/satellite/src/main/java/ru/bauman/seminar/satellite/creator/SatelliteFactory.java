package ru.bauman.seminar.satellite.creator;

import ru.bauman.seminar.satellite.creator.param.SatelliteParam;
import ru.bauman.seminar.satellite.entity.Satellite;
import ru.bauman.seminar.satellite.entity.SatelliteType;

public interface SatelliteFactory {
	Satellite createSatelliteWithParameter(SatelliteParam param);
	boolean isSatelliteTypeSupported(SatelliteType type);
}
