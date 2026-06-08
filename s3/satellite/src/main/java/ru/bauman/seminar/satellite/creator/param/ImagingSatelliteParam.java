package ru.bauman.seminar.satellite.creator.param;

import lombok.Getter;
import ru.bauman.seminar.satellite.entity.SatelliteType;

import java.math.BigDecimal;

@Getter
public class ImagingSatelliteParam extends SatelliteParam {
	private final BigDecimal resolution;

	public ImagingSatelliteParam(String name, BigDecimal batteryLevel, BigDecimal resolution) {
		super(SatelliteType.IMAGING, name, batteryLevel);
		this.resolution = resolution;
	}
}