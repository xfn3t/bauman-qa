package ru.bauman.seminar.constellation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.bauman.seminar.constellation.controller.dto.request.ConstellationRequest;
import ru.bauman.seminar.constellation.controller.dto.response.ConstellationResponse;
import ru.bauman.seminar.constellation.controller.dto.response.ConstellationStatusDto;
import ru.bauman.seminar.constellation.service.ConstellationService;
import ru.bauman.seminar.satellite.controller.dto.response.SatelliteStatusDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConstellationController.class)
class ConstellationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private ConstellationService constellationService;

	@Test
	void shouldReturnAllConstellations() throws Exception {
		ConstellationResponse response = new ConstellationResponse(1L, "Test", "Desc", List.of());
		when(constellationService.findAll()).thenReturn(List.of(response));

		mockMvc.perform(get("/api/constellations"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name").value("Test"))
				.andExpect(jsonPath("$[0].description").value("Desc"));
	}

	@Test
	void shouldCreateConstellation() throws Exception {
		ConstellationRequest request = new ConstellationRequest("Test", "Desc");
		ConstellationResponse response = new ConstellationResponse(1L, "Test", "Desc", List.of());

		when(constellationService.create(any(ConstellationRequest.class))).thenReturn(response);

		mockMvc.perform(post("/api/constellations")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(header().exists("Location"))
				.andExpect(jsonPath("$.name").value("Test"));
	}

	@Test
	void shouldReturnBadRequestForInvalidConstellation() throws Exception {
		ConstellationRequest request = new ConstellationRequest("", "");

		mockMvc.perform(post("/api/constellations")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void shouldGetConstellationById() throws Exception {
		ConstellationResponse response = new ConstellationResponse(1L, "Test", "Desc", List.of());
		when(constellationService.findById(anyLong())).thenReturn(response);

		mockMvc.perform(get("/api/constellations/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Test"));
	}

	@Test
	void shouldGetConstellationStatusById() throws Exception {
		ConstellationStatusDto status = ConstellationStatusDto.builder()
				.id(1L)
				.name("Test")
				.description("Desc")
				.satellitesCount(2)
				.satellites(List.of(
						SatelliteStatusDto.builder().id(10L).name("Sat1").build(),
						SatelliteStatusDto.builder().id(11L).name("Sat2").build()
				))
				.build();

		when(constellationService.getConstellationStatus(1L)).thenReturn(status);

		mockMvc.perform(get("/api/constellations/1/status"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Test"))
				.andExpect(jsonPath("$.satellitesCount").value(2))
				.andExpect(jsonPath("$.satellites[0].id").value(10));
	}
}