package ru.bauman.seminar.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.bauman.seminar.common.exception.dto.ErrorResponse;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
		log.warn("Сущность не найдена: {}", ex.getMessage());
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(ErrorResponse.of(HttpStatus.NOT_FOUND, ex.getMessage()));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
		log.warn("Некорректный аргумент: {}", ex.getMessage());
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(ErrorResponse.of(HttpStatus.BAD_REQUEST, ex.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
		String errorMessage = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage())
				.collect(Collectors.joining(", "));

		log.warn("Ошибка валидации: {}", errorMessage);

		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(ErrorResponse.of(HttpStatus.BAD_REQUEST, "Ошибка валидации: " + errorMessage));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
		log.error("Внутренняя ошибка сервера", ex);
		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ErrorResponse.of(
						HttpStatus.INTERNAL_SERVER_ERROR,
						"Внутренняя ошибка сервера"
				));
	}
}