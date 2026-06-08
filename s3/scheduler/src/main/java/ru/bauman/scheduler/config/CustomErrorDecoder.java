package ru.bauman.scheduler.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class CustomErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultDecoder = new ErrorDecoder.Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());
        log.error("Feign ошибка: {} {} - {}", methodKey, status, response.reason());
        if (status.is4xxClientError()) {
            return new IllegalArgumentException("Ошибка запроса: " + response.reason());
        }
        if (status.is5xxServerError()) {
            return new RuntimeException("Серверная ошибка: " + response.reason());
        }
        return defaultDecoder.decode(methodKey, response);
    }
}