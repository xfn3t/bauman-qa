package ru.bmstu.storing.service.dto;

import java.util.UUID;

public record WorkDto(
        UUID workId,
        String studentName,
        String fileName,
        Long fileSize,
        String contentType,
        String s3Key
) {}
