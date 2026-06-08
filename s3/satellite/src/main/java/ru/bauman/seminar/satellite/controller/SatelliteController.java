package ru.bauman.seminar.satellite.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bauman.seminar.satellite.controller.dto.request.SatelliteRequest;
import ru.bauman.seminar.satellite.controller.dto.response.SatelliteResponse;
import ru.bauman.seminar.satellite.service.SatelliteService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/satellites")
@RequiredArgsConstructor
@Tag(name = "Спутники", description = "Управление спутниками")
public class SatelliteController {

	private final SatelliteService satelliteService;

	@GetMapping
	@Operation(summary = "Получить все спутники", description = "Возвращает список всех спутников")
	@ApiResponse(responseCode = "200", description = "Список спутников успешно получен")
	public List<SatelliteResponse> getAllSatellites() {
		return satelliteService.findAll();
	}

	@GetMapping("/{id}")
	@Operation(summary = "Получить спутник по ID", description = "Возвращает данные спутника по его идентификатору")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Спутник найден"),
			@ApiResponse(responseCode = "404", description = "Спутник не найден")
	})
	public SatelliteResponse getSatelliteById(
			@Parameter(description = "Идентификатор спутника", example = "1") @PathVariable Long id) {
		return satelliteService.findById(id);
	}

	@GetMapping("/by-name/{name}")
	@Operation(summary = "Получить спутник по названию", description = "Возвращает данные спутника по его названию")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Спутник найден"),
			@ApiResponse(responseCode = "404", description = "Спутник не найден")
	})
	public SatelliteResponse getSatelliteByName(
			@Parameter(description = "Название спутника", example = "Sputnik-1") @PathVariable String name) {
		return satelliteService.findByName(name);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Создать новый спутник", description = "Создаёт спутник на основе переданных данных")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Спутник успешно создан"),
			@ApiResponse(responseCode = "400", description = "Некорректные входные данные")
	})
	public ResponseEntity<SatelliteResponse> createSatellite(@Valid @RequestBody SatelliteRequest request) {
		SatelliteResponse created = satelliteService.create(request);
		return ResponseEntity
				.created(URI.create("/api/satellites/" + created.id()))
				.body(created);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Обновить спутник", description = "Обновляет данные существующего спутника")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Спутник обновлён"),
			@ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
			@ApiResponse(responseCode = "404", description = "Спутник не найден")
	})
	public SatelliteResponse updateSatellite(
			@Parameter(description = "Идентификатор спутника", example = "1") @PathVariable Long id,
			@Valid @RequestBody SatelliteRequest request) {
		return satelliteService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Удалить спутник", description = "Удаляет спутник по его идентификатору")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Спутник удалён"),
			@ApiResponse(responseCode = "404", description = "Спутник не найден")
	})
	public void deleteSatellite(
			@Parameter(description = "Идентификатор спутника", example = "1") @PathVariable Long id) {
		satelliteService.delete(id);
	}

	@PostMapping("/{id}/activate")
	@Operation(summary = "Активировать спутник", description = "Переводит спутник в активный режим")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Спутник активирован"),
			@ApiResponse(responseCode = "400", description = "Спутник не может быть активирован (например, низкий заряд)"),
			@ApiResponse(responseCode = "404", description = "Спутник не найден")
	})
	public SatelliteResponse activateSatellite(
			@Parameter(description = "Идентификатор спутника", example = "1") @PathVariable Long id) {
		return satelliteService.activate(id);
	}

	@PostMapping("/{id}/deactivate")
	@Operation(summary = "Деактивировать спутник", description = "Переводит спутник в неактивный режим")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Спутник деактивирован"),
			@ApiResponse(responseCode = "404", description = "Спутник не найден")
	})
	public SatelliteResponse deactivateSatellite(
			@Parameter(description = "Идентификатор спутника", example = "1") @PathVariable Long id) {
		return satelliteService.deactivate(id);
	}

	@PostMapping("/{id}/mission")
	@Operation(summary = "Выполнить миссию", description = "Выполняет миссию спутника (фотосъёмку или передачу данных)")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Миссия выполнена"),
			@ApiResponse(responseCode = "400", description = "Миссия не может быть выполнена (неактивен, низкий заряд)"),
			@ApiResponse(responseCode = "404", description = "Спутник не найден")
	})
	public SatelliteResponse performMission(
			@Parameter(description = "Идентификатор спутника", example = "1") @PathVariable Long id) {
		return satelliteService.performMission(id);
	}
}