package ru.bauman.seminar.satellite.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.bauman.seminar.satellite.controller.dto.request.SatelliteRequest;
import ru.bauman.seminar.satellite.controller.dto.response.SatelliteResponse;
import ru.bauman.seminar.satellite.entity.SatelliteState;
import ru.bauman.seminar.satellite.entity.SatelliteType;
import ru.bauman.seminar.satellite.service.SatelliteService;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SatelliteController.class)
class SatelliteControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private SatelliteService satelliteService;

	@Test
	void shouldCreateCommunicationSatellite() throws Exception {
		SatelliteRequest request = new SatelliteRequest(
				"Comm-1", new BigDecimal("0.85"), SatelliteType.COMMUNICATION,
				new BigDecimal("500.0"), null
		);

		SatelliteResponse response = new SatelliteResponse(
				1L, "Comm-1", new BigDecimal("0.85"), SatelliteState.ACTIVE, SatelliteType.COMMUNICATION,
				new BigDecimal("500.0"), null, 0
		);

		when(satelliteService.create(any(SatelliteRequest.class))).thenReturn(response);

		mockMvc.perform(post("/api/satellites")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(header().exists("Location"))
				.andExpect(jsonPath("$.name").value("Comm-1"))
				.andExpect(jsonPath("$.type").value("COMMUNICATION"));
	}

	@Test
	void shouldReturnBadRequestForInvalidSatellite() throws Exception {
		SatelliteRequest request = new SatelliteRequest(
				"", new BigDecimal("-1.0"), null, null, null
		);

		mockMvc.perform(post("/api/satellites")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void shouldActivateSatellite() throws Exception {
		SatelliteResponse response = new SatelliteResponse(
				1L, "Sat-1", new BigDecimal("0.85"), SatelliteState.ACTIVE, SatelliteType.COMMUNICATION,
				new BigDecimal("500.0"), null, 0
		);

		when(satelliteService.activate(anyLong())).thenReturn(response);

		mockMvc.perform(post("/api/satellites/1/activate"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.state").value("ACTIVE"));
	}

	@Test
	void shouldPerformMission() throws Exception {
		SatelliteResponse response = new SatelliteResponse(
				1L, "Sat-1", new BigDecimal("0.77"), SatelliteState.ACTIVE, SatelliteType.COMMUNICATION,
				new BigDecimal("500.0"), null, 0
		);

		when(satelliteService.performMission(anyLong())).thenReturn(response);

		mockMvc.perform(post("/api/satellites/1/mission"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.batteryLevel").value(0.77));
	}
}