package ru.bauman.seminar.constellation.service;

import ru.bauman.seminar.constellation.controller.dto.response.ConstellationStatusDto;

public interface ConstellationStatusProvider {
	ConstellationStatusDto getConstellationStatus(String constellationName);
}