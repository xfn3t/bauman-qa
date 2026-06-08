package ru.bauman.seminar.satellite.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import ru.bauman.seminar.common.BaseIntegrationTest;
import ru.bauman.seminar.satellite.controller.dto.request.SatelliteRequest;
import ru.bauman.seminar.satellite.controller.dto.response.SatelliteResponse;
import ru.bauman.seminar.satellite.entity.SatelliteState;
import ru.bauman.seminar.satellite.entity.SatelliteType;
import ru.bauman.seminar.satellite.repository.SatelliteRepository;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
class SatelliteIntegrationTest extends BaseIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private SatelliteRepository satelliteRepository;

	@BeforeEach
	void setUp() {
		satelliteRepository.deleteAll();
	}

	@Test
	void shouldCreateCommunicationSatellite() {
		SatelliteRequest request = new SatelliteRequest(
				"Comm-1", new BigDecimal("0.85"), SatelliteType.COMMUNICATION,
				new BigDecimal("500.0"), null
		);

		var response = restTemplate.postForEntity("/api/satellites", request, SatelliteResponse.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().name()).isEqualTo("Comm-1");
		assertThat(response.getBody().type()).isEqualTo(SatelliteType.COMMUNICATION);
		assertThat(response.getBody().bandwidth()).isEqualTo(new BigDecimal("500.0"));
	}

	@Test
	void shouldCreateImagingSatellite() {
		SatelliteRequest request = new SatelliteRequest(
				"Img-1", new BigDecimal("0.92"), SatelliteType.IMAGING,
				null, new BigDecimal("2.5")
		);

		var response = restTemplate.postForEntity("/api/satellites", request, SatelliteResponse.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().name()).isEqualTo("Img-1");
		assertThat(response.getBody().type()).isEqualTo(SatelliteType.IMAGING);
		assertThat(response.getBody().resolution()).isEqualTo(new BigDecimal("2.5"));
	}

	@Test
	void shouldActivateSatellite() {
		SatelliteRequest request = new SatelliteRequest(
				"Sat-1", new BigDecimal("0.85"), SatelliteType.COMMUNICATION,
				new BigDecimal("500.0"), null
		);
		var createResponse = restTemplate.postForEntity("/api/satellites", request, SatelliteResponse.class);
		Long satelliteId = createResponse.getBody().id();

		var activateResponse = restTemplate.postForEntity("/api/satellites/" + satelliteId + "/activate", null, SatelliteResponse.class);

		assertThat(activateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(activateResponse.getBody()).isNotNull();
		assertThat(activateResponse.getBody().state()).isEqualTo(SatelliteState.ACTIVE);
	}

	@Test
	void shouldNotActivateLowBatterySatellite() {
		SatelliteRequest request = new SatelliteRequest(
				"Sat-1", new BigDecimal("0.1"), SatelliteType.COMMUNICATION,
				new BigDecimal("500.0"), null
		);
		var createResponse = restTemplate.postForEntity("/api/satellites", request, SatelliteResponse.class);
		Long satelliteId = createResponse.getBody().id();

		var activateResponse = restTemplate.postForEntity("/api/satellites/" + satelliteId + "/activate", null, SatelliteResponse.class);

		assertThat(activateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(activateResponse.getBody()).isNotNull();
		// При низком заряде активация не происходит, состояние остаётся INACTIVE
		assertThat(activateResponse.getBody().state()).isEqualTo(SatelliteState.INACTIVE);
	}
}