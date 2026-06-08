package ru.bauman.seminar.common.exception.dto;

import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Builder
public record ErrorResponse(
		LocalDateTime timestamp,
		int status,
		String error,
		String message
) {
	public static ErrorResponse of(HttpStatus status, String message) {
		return ErrorResponse.builder()
				.timestamp(LocalDateTime.now())
				.status(status.value())
				.error(status.getReasonPhrase())
				.message(message)
				.build();
	}
}