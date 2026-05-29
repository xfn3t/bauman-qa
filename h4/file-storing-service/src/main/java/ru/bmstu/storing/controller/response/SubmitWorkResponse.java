package ru.bmstu.storing.controller.response;

import java.util.UUID;

public record SubmitWorkResponse(
        UUID workId,
        String status,
        String message
) {}
