package ru.bauman.seminar.satellite.creator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.bauman.seminar.common.BaseIntegrationTest;
import ru.bauman.seminar.satellite.creator.param.CommunicationSatelliteParam;
import ru.bauman.seminar.satellite.creator.param.ImagingSatelliteParam;
import ru.bauman.seminar.satellite.entity.Satellite;
import ru.bauman.seminar.satellite.entity.ext.CommunicationSatellite;
import ru.bauman.seminar.satellite.entity.ext.ImagingSatellite;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Интеграционные тесты SatelliteCreationService")
class SatelliteCreationServiceTest extends BaseIntegrationTest {

	@Autowired
	private SatelliteCreationService creationService;

	@Test
	@DisplayName("Сервис создает CommunicationSatellite по CommunicationSatelliteParam")
	void shouldCreateCommunicationSatellite() {
		var param = new CommunicationSatelliteParam("Связь-Test", new BigDecimal("0.75"), new BigDecimal("1000.0"));

		Satellite satellite = creationService.createSatellite(param);

		assertThat(satellite).isInstanceOf(CommunicationSatellite.class);
		CommunicationSatellite comm = (CommunicationSatellite) satellite;
		assertThat(comm.getName()).isEqualTo("Связь-Test");
		assertThat(comm.getBatteryLevel()).isEqualByComparingTo("0.75");
		assertThat(comm.getBandwidth()).isEqualByComparingTo("1000.0");
	}

	@Test
	@DisplayName("Сервис создаёт ImagingSatellite по ImagingSatelliteParam")
	void shouldCreateImagingSatellite() {
		var param = new ImagingSatelliteParam("ДЗЗ-Test", new BigDecimal("0.90"), new BigDecimal("0.5"));

		Satellite satellite = creationService.createSatellite(param);

		assertThat(satellite).isInstanceOf(ImagingSatellite.class);
		ImagingSatellite img = (ImagingSatellite) satellite;
		assertThat(img.getName()).isEqualTo("ДЗЗ-Test");
		assertThat(img.getBatteryLevel()).isEqualByComparingTo("0.90");
		assertThat(img.getResolution()).isEqualByComparingTo("0.5");
		assertThat(img.getPhotosTaken()).isZero();
	}
}
