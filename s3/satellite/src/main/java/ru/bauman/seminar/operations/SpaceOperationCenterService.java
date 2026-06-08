package ru.bauman.seminar.operations;

import ru.bauman.seminar.constellation.controller.dto.response.ConstellationResponse;
import ru.bauman.seminar.operations.dto.AddSatelliteRequest;
import ru.bauman.seminar.operations.dto.MissionRequest;
import ru.bauman.seminar.operations.dto.MissionResult;
import ru.bauman.seminar.operations.dto.SystemStatusDto;

public interface SpaceOperationCenterService {

	ConstellationResponse addSatellites(AddSatelliteRequest request);

	MissionResult executeMission(MissionRequest request);

	MissionResult activateAndExecuteAll(String constellationName);

	SystemStatusDto getSystemStatus();
}