package ru.bauman.seminar.operations.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bauman.seminar.operations.SpaceOperationCenterService;
import ru.bauman.seminar.operations.dto.MissionRequest;
import ru.bauman.seminar.operations.dto.MissionResult;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SpaceOperationController {

    private final SpaceOperationCenterService spaceOperationCenterService;

    @PostMapping("/missions")
    public ResponseEntity<MissionResult> executeMission(@Valid @RequestBody MissionRequest request) {
        MissionResult result = spaceOperationCenterService.executeMission(request);
        return ResponseEntity.ok(result);
    }
}