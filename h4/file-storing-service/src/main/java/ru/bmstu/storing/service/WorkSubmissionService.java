package ru.bmstu.storing.service;

import ru.bmstu.storing.controller.request.SubmitWorkRequest;
import ru.bmstu.storing.controller.response.SubmitWorkResponse;
import ru.bmstu.storing.service.dto.WorkDto;

import java.util.Optional;
import java.util.UUID;

public interface WorkSubmissionService {
    SubmitWorkResponse submitWork(SubmitWorkRequest request);
    Optional<WorkDto> getWorkById(UUID workId);
}
