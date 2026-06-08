package ru.bauman.seminar.satellite.creator.param;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.bauman.seminar.satellite.entity.SatelliteType;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public abstract class SatelliteParam {
	private final SatelliteType type;
	private final String name;
	private final BigDecimal batteryLevel;
}
