package ru.bauman.seminar.constellation.controller;

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
import ru.bauman.seminar.constellation.controller.dto.request.ConstellationRequest;
import ru.bauman.seminar.constellation.controller.dto.response.ConstellationResponse;
import ru.bauman.seminar.constellation.controller.dto.response.ConstellationStatusDto;
import ru.bauman.seminar.constellation.service.ConstellationService;
import ru.bauman.seminar.satellite.controller.dto.request.SatelliteRequest;
import ru.bauman.seminar.satellite.controller.dto.response.SatelliteResponse;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/constellations")
@RequiredArgsConstructor
@Tag(name = "Группировки", description = "Управление спутниковыми группировками")
public class ConstellationController {

	private final ConstellationService constellationService;

	@GetMapping
	@Operation(summary = "Получить все группировки", description = "Возвращает список всех группировок")
	@ApiResponse(responseCode = "200", description = "Список группировок успешно получен")
	public List<ConstellationResponse> getAllConstellations() {
		return constellationService.findAll();
	}

	@GetMapping("/{id}")
	@Operation(summary = "Получить группировку по ID", description = "Возвращает данные группировки по её идентификатору")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Группировка найдена"),
			@ApiResponse(responseCode = "404", description = "Группировка не найдена")
	})
	public ConstellationResponse getConstellationById(
			@Parameter(description = "Идентификатор группировки", example = "1") @PathVariable Long id) {
		return constellationService.findById(id);
	}

	@GetMapping("/{id}/status")
	@Operation(summary = "Получить статус группировки", description = "Возвращает расширенный статус группировки с информацией о всех спутниках")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Статус получен"),
			@ApiResponse(responseCode = "404", description = "Группировка не найдена")
	})
	public ResponseEntity<ConstellationStatusDto> getConstellationStatus(
			@Parameter(description = "Идентификатор группировки", example = "1") @PathVariable Long id) {
		return ResponseEntity.ok(constellationService.getConstellationStatus(id));
	}

	@GetMapping("/by-name/{name}")
	@Operation(summary = "Получить группировку по названию", description = "Возвращает данные группировки по её названию")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Группировка найдена"),
			@ApiResponse(responseCode = "404", description = "Группировка не найдена")
	})
	public ConstellationResponse getConstellationByName(
			@Parameter(description = "Название группировки", example = "Starlink") @PathVariable String name) {
		return constellationService.findByName(name);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Создать новую группировку", description = "Создаёт группировку на основе переданных данных")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Группировка успешно создана"),
			@ApiResponse(responseCode = "400", description = "Некорректные входные данные")
	})
	public ResponseEntity<ConstellationResponse> createConstellation(
			@Valid @RequestBody ConstellationRequest request) {
		ConstellationResponse created = constellationService.create(request);
		return ResponseEntity
				.created(URI.create("/api/constellations/" + created.id()))
				.body(created);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Обновить группировку", description = "Обновляет данные существующей группировки")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Группировка обновлена"),
			@ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
			@ApiResponse(responseCode = "404", description = "Группировка не найдена")
	})
	public ConstellationResponse updateConstellation(
			@Parameter(description = "Идентификатор группировки", example = "1") @PathVariable Long id,
			@Valid @RequestBody ConstellationRequest request) {
		return constellationService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Удалить группировку", description = "Удаляет группировку по её идентификатору")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Группировка удалена"),
			@ApiResponse(responseCode = "404", description = "Группировка не найдена")
	})
	public void deleteConstellation(
			@Parameter(description = "Идентификатор группировки", example = "1") @PathVariable Long id) {
		constellationService.delete(id);
	}

	@PostMapping("/{id}/satellites")
	@Operation(summary = "Добавить спутник в группировку", description = "Создаёт новый спутник и добавляет его в указанную группировку")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Спутник добавлен"),
			@ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
			@ApiResponse(responseCode = "404", description = "Группировка не найдена")
	})
	public ConstellationResponse addSatellite(
			@Parameter(description = "Идентификатор группировки", example = "1") @PathVariable Long id,
			@Valid @RequestBody SatelliteRequest request) {
		return constellationService.addSatellite(id, request);
	}

	@DeleteMapping("/{id}/satellites/{satelliteId}")
	@Operation(summary = "Удалить спутник из группировки", description = "Удаляет спутник из группировки (сам спутник тоже удаляется)")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Спутник удалён из группировки"),
			@ApiResponse(responseCode = "404", description = "Группировка или спутник не найдены")
	})
	public ConstellationResponse removeSatellite(
			@Parameter(description = "Идентификатор группировки", example = "1") @PathVariable Long id,
			@Parameter(description = "Идентификатор спутника", example = "10") @PathVariable Long satelliteId) {
		return constellationService.removeSatellite(id, satelliteId);
	}

	@GetMapping("/{id}/satellites")
	@Operation(summary = "Получить все спутники группировки", description = "Возвращает список спутников, входящих в группировку")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Список спутников получен"),
			@ApiResponse(responseCode = "404", description = "Группировка не найдена")
	})
	public List<SatelliteResponse> getSatellites(
			@Parameter(description = "Идентификатор группировки", example = "1") @PathVariable Long id) {
		return constellationService.getSatellites(id);
	}

	@PostMapping("/{id}/activate")
	@Operation(summary = "Активировать все спутники группировки", description = "Активирует все спутники в группировке")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Все спутники активированы (где это возможно)"),
			@ApiResponse(responseCode = "404", description = "Группировка не найдена")
	})
	public List<SatelliteResponse> activateAllSatellites(
			@Parameter(description = "Идентификатор группировки", example = "1") @PathVariable Long id) {
		return constellationService.activateAllSatellites(id);
	}

	@PostMapping("/{id}/execute")
	@Operation(summary = "Выполнить миссии всех спутников", description = "Запускает выполнение миссии для всех активных спутников группировки")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Миссии выполнены (где возможно)"),
			@ApiResponse(responseCode = "404", description = "Группировка не найдена")
	})
	public List<SatelliteResponse> executeAllMissions(
			@Parameter(description = "Идентификатор группировки", example = "1") @PathVariable Long id) {
		return constellationService.executeAllMissions(id);
	}
}