package ru.bmstu.storing.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.InputStream;

@Getter
@Setter
@NoArgsConstructor
public class SubmitWorkRequest {
    private String studentName;
    private String fileName;
    private Long fileSize;
    private String contentType;
    private byte[] fileContent;
    private InputStream inputStream;
}
