package ru.bauman.seminar.constellation.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bauman.seminar.common.exception.EntityNotFoundException;
import ru.bauman.seminar.constellation.controller.dto.request.ConstellationRequest;
import ru.bauman.seminar.constellation.controller.dto.response.ConstellationResponse;
import ru.bauman.seminar.constellation.creator.ConstellationFactory;
import ru.bauman.seminar.constellation.entity.Constellation;
import ru.bauman.seminar.constellation.mapper.ConstellationMapper;
import ru.bauman.seminar.constellation.mapper.ConstellationStatusMapper;
import ru.bauman.seminar.constellation.service.entity.ConstellationEntityService;
import ru.bauman.seminar.satellite.controller.dto.request.SatelliteRequest;
import ru.bauman.seminar.satellite.controller.dto.response.SatelliteResponse;
import ru.bauman.seminar.satellite.entity.Satellite;
import ru.bauman.seminar.satellite.entity.SatelliteType;
import ru.bauman.seminar.satellite.service.SatelliteService;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Мок-тесты для ConstellationServiceImpl")
class ConstellationServiceImplTest {

	@Mock
	private ConstellationEntityService constellationEntityService;

	@Mock
	private ConstellationFactory constellationFactory;

	@Mock
	private SatelliteService satelliteService;

	@Mock
	private ConstellationMapper constellationMapper;

	@Mock
	private ConstellationStatusMapper constellationStatusMapper;

	@InjectMocks
	private ConstellationServiceImpl constellationService;

	private final Long CONSTELLATION_ID = 1L;
	private final String CONSTELLATION_NAME = "TestGroup";
	private Constellation constellation;
	private ConstellationResponse constellationResponse;

	@BeforeEach
	void setUp() {
		constellation = Constellation.builder()
				.name(CONSTELLATION_NAME)
				.description("Desc")
				.build();
		constellation.setId(CONSTELLATION_ID);

		constellationResponse = new ConstellationResponse(
				CONSTELLATION_ID, CONSTELLATION_NAME, "Desc", List.of()
		);
	}

	@Test
	@DisplayName("findById возвращает группировку")
	void findById_ShouldReturnConstellation() {
		when(constellationEntityService.findByIdWithSatellites(CONSTELLATION_ID))
				.thenReturn(constellation);
		when(constellationMapper.toResponse(constellation)).thenReturn(constellationResponse);

		ConstellationResponse result = constellationService.findById(CONSTELLATION_ID);

		assertThat(result).isEqualTo(constellationResponse);
		verify(constellationEntityService).findByIdWithSatellites(CONSTELLATION_ID);
	}

	@Test
	@DisplayName("create создаёт новую группировку")
	void create_ShouldSaveAndReturnConstellation() {
		ConstellationRequest request = new ConstellationRequest("New", "NewDesc");
		Constellation newConstellation = Constellation.builder()
				.name("New")
				.description("NewDesc")
				.build();
		Constellation savedConstellation = Constellation.builder()
				.name("New")
				.description("NewDesc")
				.build();
		savedConstellation.setId(2L);

		ConstellationResponse expectedResponse = new ConstellationResponse(2L, "New", "NewDesc", List.of());

		when(constellationFactory.createConstellation(request.name(), request.description()))
				.thenReturn(newConstellation);
		when(constellationEntityService.save(newConstellation)).thenReturn(savedConstellation);
		when(constellationMapper.toResponse(savedConstellation)).thenReturn(expectedResponse);

		ConstellationResponse result = constellationService.create(request);

		assertThat(result).isEqualTo(expectedResponse);
		verify(constellationFactory).createConstellation(request.name(), request.description());
		verify(constellationEntityService).save(newConstellation);
	}

	@Test
	@DisplayName("addSatellite добавляет спутник в группировку")
	void addSatellite_ShouldAddSatellite() {
		SatelliteRequest satelliteRequest = new SatelliteRequest(
				"Comm1", BigDecimal.valueOf(0.9), SatelliteType.COMMUNICATION,
				BigDecimal.valueOf(500), null
		);
		Satellite satellite = mock(Satellite.class);

		Constellation constellationWithSatellites = Constellation.builder()
				.name(CONSTELLATION_NAME)
				.description("Desc")
				.build();
		constellationWithSatellites.setId(CONSTELLATION_ID);
		constellationWithSatellites.getSatellites().add(satellite);

		ConstellationResponse expectedResponse = new ConstellationResponse(
				CONSTELLATION_ID, CONSTELLATION_NAME, "Desc",
				List.of(mock(SatelliteResponse.class))
		);

		when(constellationEntityService.findByIdWithSatellites(CONSTELLATION_ID))
				.thenReturn(constellation);
		when(satelliteService.createEntity(satelliteRequest)).thenReturn(satellite);
		when(constellationEntityService.save(constellation)).thenReturn(constellationWithSatellites);
		when(constellationMapper.toResponse(constellationWithSatellites)).thenReturn(expectedResponse);

		ConstellationResponse result = constellationService.addSatellite(CONSTELLATION_ID, satelliteRequest);

		assertThat(result).isEqualTo(expectedResponse);
		verify(satellite).setConstellation(constellation);
		verify(constellationEntityService).save(constellation);
	}

	@Test
	@DisplayName("removeSatellite удаляет спутник из группировки")
	void removeSatellite_ShouldRemoveSatellite() {
		Long satelliteId = 10L;
		Satellite satellite = mock(Satellite.class);
		when(satellite.getId()).thenReturn(satelliteId);
		constellation.getSatellites().add(satellite);

		Constellation constellationAfterRemoval = Constellation.builder()
				.name(CONSTELLATION_NAME)
				.description("Desc")
				.build();
		constellationAfterRemoval.setId(CONSTELLATION_ID);

		ConstellationResponse expectedResponse = new ConstellationResponse(
				CONSTELLATION_ID, CONSTELLATION_NAME, "Desc", List.of()
		);

		when(constellationEntityService.findByIdWithSatellites(CONSTELLATION_ID))
				.thenReturn(constellation);
		when(constellationEntityService.save(constellation)).thenReturn(constellationAfterRemoval);
		when(constellationMapper.toResponse(constellationAfterRemoval)).thenReturn(expectedResponse);

		ConstellationResponse result = constellationService.removeSatellite(CONSTELLATION_ID, satelliteId);

		assertThat(result).isEqualTo(expectedResponse);
		verify(satellite).setConstellation(null);
		verify(constellationEntityService).save(constellation);
	}

	@Test
	@DisplayName("removeSatellite кидает исключение, если спутник не в группировке")
	void removeSatellite_ShouldThrowIfSatelliteNotFound() {
		Long wrongSatelliteId = 99L;
		when(constellationEntityService.findByIdWithSatellites(CONSTELLATION_ID))
				.thenReturn(constellation);

		assertThatThrownBy(() -> constellationService.removeSatellite(CONSTELLATION_ID, wrongSatelliteId))
				.isInstanceOf(EntityNotFoundException.class)
				.hasMessageContaining("не найден в группировке");
	}

	@Test
	@DisplayName("activateAllSatellites активирует все спутники")
	void activateAllSatellites_ShouldActivateAll() {
		Satellite sat1 = mock(Satellite.class);
		Satellite sat2 = mock(Satellite.class);
		when(sat1.getId()).thenReturn(1L);
		when(sat2.getId()).thenReturn(2L);
		constellation.setSatellites(List.of(sat1, sat2));

		SatelliteResponse resp1 = mock(SatelliteResponse.class);
		SatelliteResponse resp2 = mock(SatelliteResponse.class);

		when(constellationEntityService.findByIdWithSatellites(CONSTELLATION_ID))
				.thenReturn(constellation);
		when(satelliteService.activate(any())).thenReturn(resp1, resp2);

		List<SatelliteResponse> result = constellationService.activateAllSatellites(CONSTELLATION_ID);

		assertThat(result).containsExactly(resp1, resp2);
		verify(satelliteService).activate(1L);
		verify(satelliteService).activate(2L);
	}

	@Test
	@DisplayName("executeAllMissions выполняет миссии активных спутников")
	void executeAllMissions_ShouldExecuteMissions() {
		Satellite sat1 = mock(Satellite.class);
		Satellite sat2 = mock(Satellite.class);
		when(sat1.getId()).thenReturn(1L);
		when(sat2.getId()).thenReturn(2L);
		constellation.setSatellites(List.of(sat1, sat2));

		SatelliteResponse resp1 = mock(SatelliteResponse.class);
		SatelliteResponse resp2 = mock(SatelliteResponse.class);

		when(constellationEntityService.findByIdWithSatellites(CONSTELLATION_ID))
				.thenReturn(constellation);
		when(satelliteService.performMission(any())).thenReturn(resp1, resp2);

		List<SatelliteResponse> result = constellationService.executeAllMissions(CONSTELLATION_ID);

		assertThat(result).containsExactly(resp1, resp2);
		verify(satelliteService).performMission(1L);
		verify(satelliteService).performMission(2L);
	}
}