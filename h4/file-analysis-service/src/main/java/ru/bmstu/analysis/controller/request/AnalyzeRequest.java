package ru.bmstu.analysis.controller.request;

import java.util.UUID;

public record AnalyzeRequest(
        UUID workId,
        String studentName,
        String fileName,
        Long fileSize,
        String contentType,
        String s3Key
) {}
