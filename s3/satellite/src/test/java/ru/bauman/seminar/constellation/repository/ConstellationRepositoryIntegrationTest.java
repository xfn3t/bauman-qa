package ru.bauman.seminar.constellation.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import ru.bauman.seminar.constellation.controller.dto.request.ConstellationRequest;
import ru.bauman.seminar.constellation.entity.Constellation;
import ru.bauman.seminar.constellation.service.ConstellationService;
import ru.bauman.seminar.satellite.controller.dto.request.SatelliteRequest;
import ru.bauman.seminar.satellite.entity.SatelliteState;
import ru.bauman.seminar.satellite.entity.SatelliteType;
import ru.bauman.seminar.satellite.entity.ext.ImagingSatellite;
import ru.bauman.seminar.common.BaseIntegrationTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DisplayName("Интеграционные тесты ConstellationRepository")
public class ConstellationRepositoryIntegrationTest extends BaseIntegrationTest {

	@Autowired
	private ConstellationRepository constellationRepository;

	@Autowired
	private ConstellationService constellationService;

	@BeforeEach
	void setUp() {
		constellationRepository.deleteAll();
	}

	@Test
	@DisplayName("Сохранение и поиск группировки по ID")
	void shouldSaveAndFindById() {
		Constellation constellation = Constellation.builder()
				.name("Test Constellation")
				.description("Description")
				.build();

		Constellation saved = constellationRepository.save(constellation);
		assertThat(saved.getId()).isNotNull();

		Constellation found = constellationRepository.findById(saved.getId()).orElseThrow();
		assertThat(found.getName()).isEqualTo("Test Constellation");
	}

	@Test
	@DisplayName("Поиск группировки по имени")
	void shouldFindByName() {
		Constellation constellation = Constellation.builder()
				.name("UniqueName")
				.build();
		constellationRepository.save(constellation);

		Constellation found = constellationRepository.findByName("UniqueName").orElseThrow();
		assertThat(found.getName()).isEqualTo("UniqueName");
	}

	@Test
	@DisplayName("findAllWithSatellites загружает спутники")
	void shouldFindAllWithSatellites() {
		ConstellationRequest request = new ConstellationRequest("Group", "");
		var created = constellationService.create(request);

		SatelliteRequest satRequest = new SatelliteRequest(
				"Sat1", BigDecimal.valueOf(0.8), SatelliteType.COMMUNICATION,
				BigDecimal.valueOf(500), null
		);
		constellationService.addSatellite(created.id(), satRequest);

		List<Constellation> allWithSats = constellationRepository.findAllWithSatellites();
		assertThat(allWithSats).hasSize(1);
		assertThat(allWithSats.get(0).getSatellites()).hasSize(1);
	}

	@Test
	@DisplayName("Полный жизненный цикл: создание группировки → добавление спутников → активация → выполнение миссий")
	void fullLifecycleTest() {

		ConstellationRequest constellationRequest = new ConstellationRequest("Lifecycle Group", "");
		var constellation = constellationService.create(constellationRequest);
		Long constellationId = constellation.id();

		SatelliteRequest commRequest = new SatelliteRequest(
				"Comm-1", BigDecimal.valueOf(0.9), SatelliteType.COMMUNICATION,
				BigDecimal.valueOf(1000), null
		);
		SatelliteRequest imgRequest = new SatelliteRequest(
				"Img-1", BigDecimal.valueOf(0.8), SatelliteType.IMAGING,
				null, BigDecimal.valueOf(2.5)
		);
		constellationService.addSatellite(constellationId, commRequest);
		constellationService.addSatellite(constellationId, imgRequest);

		var afterAdd = constellationService.findById(constellationId);
		assertThat(afterAdd.satellites()).hasSize(2);

		// Активация спутников
		var activated = constellationService.activateAllSatellites(constellationId);
		assertThat(activated).allMatch(sat -> sat.state() == SatelliteState.ACTIVE);

		// Выполнение миссий
		var afterMission = constellationService.executeAllMissions(constellationId);
		var imagingSat = afterMission.stream()
				.filter(s -> s.type() == SatelliteType.IMAGING)
				.findFirst()
				.orElseThrow();
		assertThat(imagingSat.photosTaken()).isEqualTo(1);

		// данные сохранились
		Constellation constellationEntity = constellationRepository.findByIdWithSatellites(constellationId).orElseThrow();
		assertThat(constellationEntity.getSatellites()).hasSize(2);
		var imagingEntity = constellationEntity.getSatellites().stream()
				.filter(s -> s instanceof ImagingSatellite)
				.map(s -> (ImagingSatellite) s)
				.findFirst()
				.orElseThrow();
		assertThat(imagingEntity.getPhotosTaken()).isEqualTo(1);
	}
}