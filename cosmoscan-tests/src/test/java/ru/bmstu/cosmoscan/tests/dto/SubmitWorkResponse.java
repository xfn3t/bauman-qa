package ru.bmstu.cosmoscan.tests.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SubmitWorkResponse(
        UUID workId,
        String status,
        String message
) {}
