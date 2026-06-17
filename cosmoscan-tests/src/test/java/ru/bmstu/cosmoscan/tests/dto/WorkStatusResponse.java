package ru.bmstu.cosmoscan.tests.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WorkStatusResponse(
        String workId,
        String fileName,
        Long fileSize,
        String contentType
) {}
