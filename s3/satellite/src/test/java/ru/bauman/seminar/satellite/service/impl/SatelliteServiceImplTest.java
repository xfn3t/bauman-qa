package ru.bauman.seminar.satellite.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bauman.seminar.common.exception.EntityNotFoundException;
import ru.bauman.seminar.satellite.controller.dto.request.SatelliteRequest;
import ru.bauman.seminar.satellite.controller.dto.response.SatelliteResponse;
import ru.bauman.seminar.satellite.creator.SatelliteCreationService;
import ru.bauman.seminar.satellite.creator.SatelliteFactory;
import ru.bauman.seminar.satellite.entity.EnergySystem;
import ru.bauman.seminar.satellite.entity.Satellite;
import ru.bauman.seminar.satellite.entity.SatelliteState;
import ru.bauman.seminar.satellite.entity.SatelliteType;
import ru.bauman.seminar.satellite.entity.ext.CommunicationSatellite;
import ru.bauman.seminar.satellite.entity.ext.ImagingSatellite;
import ru.bauman.seminar.satellite.mapper.SatelliteMapper;
import ru.bauman.seminar.satellite.service.entity.SatelliteEntityService;
import ru.bauman.seminar.satellite.updater.SatelliteUpdater;
import ru.bauman.seminar.satellite.updater.impl.CommunicationSatelliteUpdater;
import ru.bauman.seminar.satellite.updater.impl.ImagingSatelliteUpdater;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SatelliteServiceImplTest {

	@Mock
	private SatelliteEntityService satelliteEntityService;

	@Mock
	private SatelliteFactory satelliteFactory;

	@Mock
	private SatelliteCreationService satelliteCreationService;

	@Mock
	private SatelliteMapper satelliteMapper;

	private SatelliteServiceImpl satelliteService;

	@BeforeEach
	void setUp() {
		List<SatelliteUpdater> realUpdaters = List.of(
				new CommunicationSatelliteUpdater(),
				new ImagingSatelliteUpdater()
		);
		satelliteService = new SatelliteServiceImpl(
				satelliteEntityService,
				satelliteCreationService,
				realUpdaters,
				satelliteMapper
		);
	}

	@Test
	void findAll_ShouldReturnAllSatellites() {
		List<Satellite> satellites = List.of(
				CommunicationSatellite.builder().id(1L).name("Comm1").state(SatelliteState.INACTIVE).build(),
				ImagingSatellite.builder().id(2L).name("Img1").state(SatelliteState.INACTIVE).build()
		);
		List<SatelliteResponse> expectedResponses = List.of(
				new SatelliteResponse(1L, "Comm1", null, SatelliteState.INACTIVE, SatelliteType.COMMUNICATION, null, null, null),
				new SatelliteResponse(2L, "Img1", null, SatelliteState.INACTIVE, SatelliteType.IMAGING, null, null, null)
		);

		when(satelliteEntityService.findAll()).thenReturn(satellites);
		when(satelliteMapper.toResponse(satellites.get(0))).thenReturn(expectedResponses.get(0));
		when(satelliteMapper.toResponse(satellites.get(1))).thenReturn(expectedResponses.get(1));

		List<SatelliteResponse> actual = satelliteService.findAll();

		assertThat(actual).hasSize(2);
		assertThat(actual).containsExactlyElementsOf(expectedResponses);
		verify(satelliteEntityService).findAll();
	}

	@Test
	void findById_ShouldReturnSatellite() {
		Long id = 1L;
		Satellite satellite = CommunicationSatellite.builder().id(id).name("Comm1").state(SatelliteState.INACTIVE).build();
		SatelliteResponse expectedResponse = new SatelliteResponse(id, "Comm1", null, SatelliteState.INACTIVE, SatelliteType.COMMUNICATION, null, null, null);

		when(satelliteEntityService.findById(id)).thenReturn(satellite);
		when(satelliteMapper.toResponse(satellite)).thenReturn(expectedResponse);

		SatelliteResponse actual = satelliteService.findById(id);

		assertThat(actual).isEqualTo(expectedResponse);
		verify(satelliteEntityService).findById(id);
	}

	@Test
	void findById_ShouldThrowWhenNotFound() {
		Long id = 99L;
		when(satelliteEntityService.findById(id)).thenThrow(new EntityNotFoundException("Спутник не найден"));

		assertThatThrownBy(() -> satelliteService.findById(id))
				.isInstanceOf(EntityNotFoundException.class)
				.hasMessageContaining("не найден");
	}

	@Test
	void findByName_ShouldReturnSatellite() {
		String name = "UniqueSat";
		Satellite satellite = ImagingSatellite.builder().id(2L).name(name).state(SatelliteState.INACTIVE).build();
		SatelliteResponse expectedResponse = new SatelliteResponse(2L, name, null, SatelliteState.INACTIVE, SatelliteType.IMAGING, null, null, null);

		when(satelliteEntityService.findByName(name)).thenReturn(satellite);
		when(satelliteMapper.toResponse(satellite)).thenReturn(expectedResponse);

		SatelliteResponse actual = satelliteService.findByName(name);

		assertThat(actual).isEqualTo(expectedResponse);
		verify(satelliteEntityService).findByName(name);
	}

	@Test
	void create_ShouldUseCreationServiceAndSave() {
		SatelliteRequest request = new SatelliteRequest(
				"NewComm", new BigDecimal("0.9"), SatelliteType.COMMUNICATION,
				new BigDecimal("600.0"), null
		);
		Satellite createdSatellite = CommunicationSatellite.builder()
				.name("NewComm")
				.energySystem(EnergySystem.builder().batteryLevel(new BigDecimal("0.9")).build())
				.state(SatelliteState.INACTIVE)
				.bandwidth(new BigDecimal("600.0"))
				.build();
		Satellite savedSatellite = CommunicationSatellite.builder()
				.id(10L)
				.name("NewComm")
				.energySystem(EnergySystem.builder().batteryLevel(new BigDecimal("0.9")).build())
				.state(SatelliteState.INACTIVE)
				.bandwidth(new BigDecimal("600.0"))
				.build();
		SatelliteResponse expectedResponse = new SatelliteResponse(
				10L, "NewComm", new BigDecimal("0.9"), SatelliteState.INACTIVE,
				SatelliteType.COMMUNICATION, new BigDecimal("600.0"), null, null
		);

		when(satelliteCreationService.createSatellite(any())).thenReturn(createdSatellite);
		when(satelliteEntityService.save(createdSatellite)).thenReturn(savedSatellite);
		when(satelliteMapper.toResponse(savedSatellite)).thenReturn(expectedResponse);

		SatelliteResponse actual = satelliteService.create(request);

		assertThat(actual).isEqualTo(expectedResponse);
		verify(satelliteCreationService).createSatellite(any());
		verify(satelliteEntityService).save(createdSatellite);
	}

	@Test
	void update_ShouldUpdateSatelliteAndCallUpdater() {
		Long id = 5L;
		SatelliteRequest request = new SatelliteRequest(
				"UpdatedComm", new BigDecimal("0.7"), SatelliteType.COMMUNICATION,
				new BigDecimal("800.0"), null
		);

		CommunicationSatellite existingSatellite = CommunicationSatellite.builder()
				.id(id)
				.name("OldName")
				.energySystem(EnergySystem.builder().batteryLevel(new BigDecimal("0.5")).build())
				.state(SatelliteState.INACTIVE)
				.bandwidth(new BigDecimal("500.0"))
				.build();

		when(satelliteEntityService.findById(id)).thenReturn(existingSatellite);
		when(satelliteEntityService.save(any(Satellite.class))).thenAnswer(inv -> inv.getArgument(0));

		SatelliteResponse expectedResponse = new SatelliteResponse(
				id, "UpdatedComm", new BigDecimal("0.70"), SatelliteState.INACTIVE,
				SatelliteType.COMMUNICATION, new BigDecimal("800.0"), null, null
		);
		when(satelliteMapper.toResponse(existingSatellite)).thenReturn(expectedResponse);

		SatelliteResponse actual = satelliteService.update(id, request);

		assertThat(actual).isEqualTo(expectedResponse);
		assertThat(existingSatellite.getName()).isEqualTo("UpdatedComm");
		assertThat(existingSatellite.getBatteryLevel()).isEqualByComparingTo("0.70");
		assertThat(existingSatellite.getBandwidth()).isEqualByComparingTo("800.0");
		verify(satelliteEntityService).save(existingSatellite);
	}

	@Test
	void update_ShouldThrowWhenTypeMismatch() {
		Long id = 5L;
		SatelliteRequest request = new SatelliteRequest(
				"Updated", new BigDecimal("0.7"), SatelliteType.IMAGING,
				null, new BigDecimal("2.0")
		);
		CommunicationSatellite existingSatellite = CommunicationSatellite.builder()
				.id(id)
				.name("Old")
				.energySystem(EnergySystem.builder().batteryLevel(new BigDecimal("0.5")).build())
				.state(SatelliteState.INACTIVE)
				.bandwidth(new BigDecimal("500.0"))
				.build();

		when(satelliteEntityService.findById(id)).thenReturn(existingSatellite);

		assertThatThrownBy(() -> satelliteService.update(id, request))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Нельзя изменить тип спутника");
		verify(satelliteEntityService, never()).save(any());
	}

	@Test
	void delete_ShouldCallEntityServiceDelete() {
		Long id = 10L;
		satelliteService.delete(id);
		verify(satelliteEntityService).delete(id);
	}

	@Test
	void activate_ShouldActivateWhenBatterySufficient() {
		Long id = 1L;
		CommunicationSatellite satellite = CommunicationSatellite.builder()
				.id(id)
				.name("Comm")
				.energySystem(EnergySystem.builder().batteryLevel(new BigDecimal("0.3")).build())
				.state(SatelliteState.INACTIVE)
				.bandwidth(new BigDecimal("500.0"))
				.build();

		SatelliteResponse expectedResponse = new SatelliteResponse(
				id, "Comm", new BigDecimal("0.3"), SatelliteState.ACTIVE,
				SatelliteType.COMMUNICATION, new BigDecimal("500.0"), null, null
		);

		when(satelliteEntityService.findById(id)).thenReturn(satellite);
		when(satelliteEntityService.save(satellite)).thenReturn(satellite);
		when(satelliteMapper.toResponse(satellite)).thenReturn(expectedResponse);

		SatelliteResponse actual = satelliteService.activate(id);

		assertThat(actual).isEqualTo(expectedResponse);
		assertThat(satellite.getState()).isEqualTo(SatelliteState.ACTIVE);
		verify(satelliteEntityService).save(satellite);
	}

	@Test
	void activate_ShouldNotActivateWhenBatteryLow() {
		Long id = 1L;
		CommunicationSatellite satellite = CommunicationSatellite.builder()
				.id(id)
				.name("Comm")
				.energySystem(EnergySystem.builder().batteryLevel(new BigDecimal("0.1")).build())
				.state(SatelliteState.INACTIVE)
				.bandwidth(new BigDecimal("500.0"))
				.build();

		SatelliteResponse expectedResponse = new SatelliteResponse(
				id, "Comm", new BigDecimal("0.1"), SatelliteState.INACTIVE,
				SatelliteType.COMMUNICATION, new BigDecimal("500.0"), null, null
		);

		when(satelliteEntityService.findById(id)).thenReturn(satellite);
		when(satelliteMapper.toResponse(satellite)).thenReturn(expectedResponse);

		SatelliteResponse actual = satelliteService.activate(id);

		assertThat(actual).isEqualTo(expectedResponse);
		assertThat(satellite.getState()).isEqualTo(SatelliteState.INACTIVE);
		verify(satelliteEntityService, never()).save(any());
	}

	@Test
	void deactivate_ShouldSetInactiveAndSave() {
		Long id = 2L;
		ImagingSatellite satellite = ImagingSatellite.builder()
				.id(id)
				.name("Img")
				.energySystem(EnergySystem.builder().batteryLevel(new BigDecimal("0.9")).build())
				.state(SatelliteState.ACTIVE)
				.resolution(new BigDecimal("2.5"))
				.photosTaken(5)
				.build();

		SatelliteResponse expectedResponse = new SatelliteResponse(
				id, "Img", new BigDecimal("0.9"), SatelliteState.INACTIVE,
				SatelliteType.IMAGING, null, new BigDecimal("2.5"), 5
		);

		when(satelliteEntityService.findById(id)).thenReturn(satellite);
		when(satelliteEntityService.save(satellite)).thenReturn(satellite);
		when(satelliteMapper.toResponse(satellite)).thenReturn(expectedResponse);

		SatelliteResponse actual = satelliteService.deactivate(id);

		assertThat(actual).isEqualTo(expectedResponse);
		assertThat(satellite.getState()).isEqualTo(SatelliteState.INACTIVE);
		verify(satelliteEntityService).save(satellite);
	}

	@Test
	void performMission_ForCommunicationSatellite_ShouldDecreaseBattery() {
		Long id = 1L;
		CommunicationSatellite satellite = CommunicationSatellite.builder()
				.id(id)
				.name("Comm")
				.energySystem(EnergySystem.builder().batteryLevel(new BigDecimal("0.5")).build())
				.state(SatelliteState.ACTIVE)
				.bandwidth(new BigDecimal("500.0"))
				.build();

		when(satelliteEntityService.findById(id)).thenReturn(satellite);
		when(satelliteEntityService.save(satellite)).thenReturn(satellite);

		SatelliteResponse expectedResponse = new SatelliteResponse(
				id, "Comm", new BigDecimal("0.45"), SatelliteState.ACTIVE,
				SatelliteType.COMMUNICATION, new BigDecimal("500.0"), null, null
		);
		when(satelliteMapper.toResponse(satellite)).thenReturn(expectedResponse);

		SatelliteResponse actual = satelliteService.performMission(id);

		assertThat(actual).isEqualTo(expectedResponse);
		assertThat(satellite.getBatteryLevel()).isEqualByComparingTo("0.45");
		assertThat(satellite.getState()).isEqualTo(SatelliteState.ACTIVE);
		verify(satelliteEntityService).save(satellite); // Сохранение должно быть вызвано
	}

	@Test
	void performMission_ForImagingSatellite_ShouldIncrementPhotosTaken() {
		Long id = 2L;
		ImagingSatellite satellite = ImagingSatellite.builder()
				.id(id)
				.name("Img")
				.energySystem(EnergySystem.builder().batteryLevel(new BigDecimal("0.9")).build())
				.state(SatelliteState.ACTIVE)
				.resolution(new BigDecimal("2.5"))
				.photosTaken(0)
				.build();

		when(satelliteEntityService.findById(id)).thenReturn(satellite);
		when(satelliteEntityService.save(satellite)).thenReturn(satellite);

		SatelliteResponse expectedResponse = new SatelliteResponse(
				id, "Img", new BigDecimal("0.82"), SatelliteState.ACTIVE,
				SatelliteType.IMAGING, null, new BigDecimal("2.5"), 1
		);
		when(satelliteMapper.toResponse(satellite)).thenReturn(expectedResponse);

		SatelliteResponse actual = satelliteService.performMission(id);

		assertThat(actual).isEqualTo(expectedResponse);
		assertThat(satellite.getPhotosTaken()).isEqualTo(1);
		assertThat(satellite.getBatteryLevel()).isEqualByComparingTo("0.82");
		assertThat(satellite.getState()).isEqualTo(SatelliteState.ACTIVE);
		verify(satelliteEntityService).save(satellite);
	}

	@Test
	void performMission_WhenBatteryBecomesCritical_ShouldSetCriticalState() {
		Long id = 1L;
		CommunicationSatellite satellite = CommunicationSatellite.builder()
				.id(id)
				.name("Comm")
				.energySystem(EnergySystem.builder()
						.batteryLevel(new BigDecimal("0.2"))
						.lowBatteryThreshold(new BigDecimal("0.2"))
						.build())
				.state(SatelliteState.ACTIVE)
				.bandwidth(new BigDecimal("500.0"))
				.build();

		when(satelliteEntityService.findById(id)).thenReturn(satellite);
		when(satelliteEntityService.save(satellite)).thenReturn(satellite);

		SatelliteResponse expectedResponse = new SatelliteResponse(
				id, "Comm", new BigDecimal("0.15"), SatelliteState.CRITICAL,
				SatelliteType.COMMUNICATION, new BigDecimal("500.0"), null, null
		);
		when(satelliteMapper.toResponse(satellite)).thenReturn(expectedResponse);

		SatelliteResponse actual = satelliteService.performMission(id);

		assertThat(actual).isEqualTo(expectedResponse);
		assertThat(satellite.getBatteryLevel()).isEqualByComparingTo("0.15");
		assertThat(satellite.getState()).isEqualTo(SatelliteState.CRITICAL);
		verify(satelliteEntityService).save(satellite);
	}

	@Test
	void performMission_WhenInactive_ShouldNotChangeStateOrBattery() {
		Long id = 1L;
		CommunicationSatellite satellite = CommunicationSatellite.builder()
				.id(id)
				.name("Comm")
				.energySystem(EnergySystem.builder().batteryLevel(new BigDecimal("0.5")).build())
				.state(SatelliteState.INACTIVE)
				.bandwidth(new BigDecimal("500.0"))
				.build();

		when(satelliteEntityService.findById(id)).thenReturn(satellite);
		when(satelliteEntityService.save(satellite)).thenReturn(satellite);

		SatelliteResponse expectedResponse = new SatelliteResponse(
				id, "Comm", new BigDecimal("0.5"), SatelliteState.INACTIVE,
				SatelliteType.COMMUNICATION, new BigDecimal("500.0"), null, null
		);
		when(satelliteMapper.toResponse(satellite)).thenReturn(expectedResponse);

		SatelliteResponse actual = satelliteService.performMission(id);

		assertThat(actual).isEqualTo(expectedResponse);
		assertThat(satellite.getBatteryLevel()).isEqualByComparingTo("0.5");
		assertThat(satellite.getState()).isEqualTo(SatelliteState.INACTIVE);

		verify(satelliteEntityService).save(satellite);
	}

	@Test
	void findByConstellationId_ShouldReturnList() {
		Long constellationId = 100L;
		List<Satellite> satellites = List.of(
				CommunicationSatellite.builder().id(1L).name("Comm1").state(SatelliteState.ACTIVE).build(),
				ImagingSatellite.builder().id(2L).name("Img1").state(SatelliteState.INACTIVE).build()
		);
		List<SatelliteResponse> expectedResponses = List.of(
				new SatelliteResponse(1L, "Comm1", null, SatelliteState.ACTIVE, SatelliteType.COMMUNICATION, null, null, null),
				new SatelliteResponse(2L, "Img1", null, SatelliteState.INACTIVE, SatelliteType.IMAGING, null, null, null)
		);

		when(satelliteEntityService.findByConstellationId(constellationId)).thenReturn(satellites);
		when(satelliteMapper.toResponse(satellites.get(0))).thenReturn(expectedResponses.get(0));
		when(satelliteMapper.toResponse(satellites.get(1))).thenReturn(expectedResponses.get(1));

		List<SatelliteResponse> actual = satelliteService.findByConstellationId(constellationId);

		assertThat(actual).hasSize(2);
		assertThat(actual).containsExactlyElementsOf(expectedResponses);
		verify(satelliteEntityService).findByConstellationId(constellationId);
	}

	@Test
	void createEntity_ShouldDelegateToCreationService() {
		SatelliteRequest request = new SatelliteRequest(
				"Test", new BigDecimal("0.5"), SatelliteType.COMMUNICATION,
				new BigDecimal("100.0"), null
		);
		Satellite expectedSatellite = CommunicationSatellite.builder().name("Test").build();

		when(satelliteCreationService.createSatellite(any())).thenReturn(expectedSatellite);

		Satellite actual = satelliteService.createEntity(request);

		assertThat(actual).isSameAs(expectedSatellite);
		verify(satelliteCreationService).createSatellite(any());
	}
}