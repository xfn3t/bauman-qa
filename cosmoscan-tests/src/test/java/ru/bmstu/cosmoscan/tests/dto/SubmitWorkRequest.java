package ru.bmstu.cosmoscan.tests.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SubmitWorkRequest(
        String studentName,
        String fileName,
        @JsonProperty("fileSize") Long fileSize,
        String contentType,
        byte[] fileContent
) {
    public static SubmitWorkRequest of(String studentName, String fileName, Long fileSize, String contentType, byte[] fileContent) {
        return new SubmitWorkRequest(studentName, fileName, fileSize, contentType, fileContent);
    }
}
