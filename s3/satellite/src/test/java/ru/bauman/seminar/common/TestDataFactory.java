package ru.bauman.seminar.common;

import ru.bauman.seminar.constellation.controller.dto.request.ConstellationRequest;
import ru.bauman.seminar.constellation.entity.Constellation;
import ru.bauman.seminar.satellite.controller.dto.request.SatelliteRequest;
import ru.bauman.seminar.satellite.entity.EnergySystem;
import ru.bauman.seminar.satellite.entity.SatelliteType;
import ru.bauman.seminar.satellite.entity.ext.CommunicationSatellite;
import ru.bauman.seminar.satellite.entity.ext.ImagingSatellite;

import java.math.BigDecimal;

public class TestDataFactory {

	public static ConstellationRequest createConstellationRequest() {
		return new ConstellationRequest("Test Constellation", "Test Description");
	}

	public static SatelliteRequest createCommunicationSatelliteRequest() {
		return new SatelliteRequest("Test Comm", new BigDecimal("0.85"), SatelliteType.COMMUNICATION,
				new BigDecimal("500.0"), null);
	}

	public static SatelliteRequest createImagingSatelliteRequest() {
		return new SatelliteRequest("Test Imaging", new BigDecimal("0.92"), SatelliteType.IMAGING,
				null, new BigDecimal("2.5"));
	}

	public static Constellation createConstellation() {
		return Constellation.builder()
				.name("Test Constellation")
				.description("Test Description")
				.build();
	}

	public static CommunicationSatellite createCommunicationSatellite() {
		return CommunicationSatellite.builder()
				.name("Test Comm")
				.energySystem(EnergySystem.builder()
						.batteryLevel(new BigDecimal("0.85"))
						.lowBatteryThreshold(new BigDecimal("0.2"))
						.build())
				.bandwidth(new BigDecimal("500.0"))
				.build();
	}

	public static ImagingSatellite createImagingSatellite() {
		return ImagingSatellite.builder()
				.name("Test Imaging")
				.energySystem(EnergySystem.builder().batteryLevel(new BigDecimal("0.92")).build())
				.resolution(new BigDecimal("2.5"))
				.photosTaken(0)
				.build();
	}
}