package ru.bauman.scheduler.dto;

public record MissionRequest(
        String constellationName,
        String satelliteName,
        MissionType missionType,
        boolean activateBeforeMission
) {}