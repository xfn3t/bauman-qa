package ru.bauman.seminar.constellation.creator.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.bauman.seminar.constellation.creator.ConstellationFactory;
import ru.bauman.seminar.constellation.entity.Constellation;
import ru.bauman.seminar.satellite.creator.SatelliteCreationService;
import ru.bauman.seminar.satellite.creator.param.CommunicationSatelliteParam;
import ru.bauman.seminar.satellite.creator.param.ImagingSatelliteParam;
import ru.bauman.seminar.satellite.entity.Satellite;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class StandardConstellationFactory implements ConstellationFactory {

	private final SatelliteCreationService satelliteCreationService;

	@Override
	public Constellation createConstellation(String name, String description) {
		return Constellation.builder()
				.name(name)
				.description(description)
				.build();
	}

	@Override
	public Constellation createDefaultConstellation() {
		return createConstellation("Default Group", "Standard empty group");
	}

	@Override
	public Constellation createConstellationWithDefaultSatellites(String name, String description) {
		Satellite commSat = satelliteCreationService.createSatellite(
				new CommunicationSatelliteParam("DefaultComm", BigDecimal.valueOf(0.9), BigDecimal.valueOf(500))
		);
		Satellite imgSat = satelliteCreationService.createSatellite(
				new ImagingSatelliteParam("DefaultImg", BigDecimal.valueOf(0.8), BigDecimal.valueOf(2.5))
		);
		return Constellation.builder()
				.name(name)
				.description(description)
				.addSatellite(commSat)
				.addSatellite(imgSat)
				.build();
	}
}