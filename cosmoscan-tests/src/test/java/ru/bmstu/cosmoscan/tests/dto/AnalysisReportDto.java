package ru.bmstu.cosmoscan.tests.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
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
