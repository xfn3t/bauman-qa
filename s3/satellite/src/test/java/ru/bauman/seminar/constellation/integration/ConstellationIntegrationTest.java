package ru.bauman.seminar.constellation.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import ru.bauman.seminar.common.BaseIntegrationTest;
import ru.bauman.seminar.constellation.controller.dto.request.ConstellationRequest;
import ru.bauman.seminar.constellation.controller.dto.response.ConstellationResponse;
import ru.bauman.seminar.constellation.repository.ConstellationRepository;
import ru.bauman.seminar.satellite.controller.dto.request.SatelliteRequest;
import ru.bauman.seminar.satellite.controller.dto.response.SatelliteResponse;
import ru.bauman.seminar.satellite.entity.SatelliteType;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
class ConstellationIntegrationTest extends BaseIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private ConstellationRepository constellationRepository;

	@BeforeEach
	void setUp() {
		constellationRepository.deleteAll();
	}

	@Test
	void shouldCreateConstellation() {
		ConstellationRequest request = new ConstellationRequest("Test Constellation", "Test Description");

		var response = restTemplate.postForEntity("/api/constellations", request, ConstellationResponse.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().name()).isEqualTo("Test Constellation");
		assertThat(response.getBody().description()).isEqualTo("Test Description");
	}

	@Test
	void shouldGetConstellationById() {
		ConstellationRequest request = new ConstellationRequest("Test", "Desc");
		var createResponse = restTemplate.postForEntity("/api/constellations", request, ConstellationResponse.class);
		Long constellationId = createResponse.getBody().id();

		var getResponse = restTemplate.getForEntity("/api/constellations/" + constellationId, ConstellationResponse.class);

		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(getResponse.getBody()).isNotNull();
		assertThat(getResponse.getBody().id()).isEqualTo(constellationId);
		assertThat(getResponse.getBody().name()).isEqualTo("Test");
	}

	@Test
	void shouldAddSatelliteToConstellation() {
		var constellationRequest = new ConstellationRequest("Test Group", "");
		var constellationResponse = restTemplate.postForEntity("/api/constellations", constellationRequest, ConstellationResponse.class);
		Long constellationId = constellationResponse.getBody().id();

		var satelliteRequest = new SatelliteRequest(
				"Test Satellite", new BigDecimal("0.8"), SatelliteType.COMMUNICATION,
				new BigDecimal("500.0"), null
		);

		var response = restTemplate.exchange(
				"/api/constellations/" + constellationId + "/satellites",
				HttpMethod.POST,
				new HttpEntity<>(satelliteRequest),
				ConstellationResponse.class
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().satellites()).hasSize(1);
		assertThat(response.getBody().satellites().get(0).name()).isEqualTo("Test Satellite");
	}

	@Test
	void shouldRemoveSatelliteFromConstellation() {
		// Создаём группировку
		var constellationRequest = new ConstellationRequest("Test Group", "");
		var constellationResponse = restTemplate.postForEntity("/api/constellations", constellationRequest, ConstellationResponse.class);
		Long constellationId = constellationResponse.getBody().id();

		// Добавляем спутник
		var satelliteRequest = new SatelliteRequest(
				"Sat To Remove", new BigDecimal("0.8"), SatelliteType.COMMUNICATION,
				new BigDecimal("500.0"), null
		);
		var addResponse = restTemplate.exchange(
				"/api/constellations/" + constellationId + "/satellites",
				HttpMethod.POST,
				new HttpEntity<>(satelliteRequest),
				ConstellationResponse.class
		);
		Long satelliteId = addResponse.getBody().satellites().get(0).id();

		// Удаляем спутник
		restTemplate.delete("/api/constellations/" + constellationId + "/satellites/" + satelliteId);

		// Проверяем, что спутник удалён из группировки
		var getResponse = restTemplate.getForEntity("/api/constellations/" + constellationId, ConstellationResponse.class);
		assertThat(getResponse.getBody().satellites()).isEmpty();

		// Проверяем, что спутник полностью удалён из БД (404 при запросе напрямую)
		var satelliteGetResponse = restTemplate.getForEntity("/api/satellites/" + satelliteId, SatelliteResponse.class);
		assertThat(satelliteGetResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void shouldReturnNotFoundForNonExistentConstellation() {
		var response = restTemplate.getForEntity("/api/constellations/999", ConstellationResponse.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}
}