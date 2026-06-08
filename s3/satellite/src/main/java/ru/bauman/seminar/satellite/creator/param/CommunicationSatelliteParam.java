package ru.bauman.seminar.satellite.creator.param;

import lombok.Getter;
import ru.bauman.seminar.satellite.entity.SatelliteType;

import java.math.BigDecimal;

@Getter
public class CommunicationSatelliteParam extends SatelliteParam {
	private final BigDecimal bandwidth;

	public CommunicationSatelliteParam(String name, BigDecimal batteryLevel, BigDecimal bandwidth) {
		super(SatelliteType.COMMUNICATION, name, batteryLevel);
		this.bandwidth = bandwidth;
	}
}