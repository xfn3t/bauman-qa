package ru.bmstu.analysis.controller.response;

import java.time.Instant;
import java.util.UUID;

public record AnalysisReportDto(
        UUID reportId,
        UUID workId,
        String fileName,
        Long fileSize,
        String format,
        String status,
        String issues,
        Instant createdAt
) {}
