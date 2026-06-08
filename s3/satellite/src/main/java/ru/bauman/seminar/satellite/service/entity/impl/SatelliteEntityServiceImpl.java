package ru.bauman.seminar.satellite.service.entity.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bauman.seminar.common.exception.EntityNotFoundException;
import ru.bauman.seminar.satellite.entity.Satellite;
import ru.bauman.seminar.satellite.repository.SatelliteRepository;
import ru.bauman.seminar.satellite.service.entity.SatelliteEntityService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SatelliteEntityServiceImpl implements SatelliteEntityService {

	private final SatelliteRepository repository;

	@Override
	@Transactional(readOnly = true)
	public List<Satellite> findAll() {
		return repository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Satellite findById(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Спутник с id " + id + " не найден"));
	}

	@Override
	@Transactional(readOnly = true)
	public Satellite findByName(String name) {
		return repository.findByName(name)
				.orElseThrow(() -> new EntityNotFoundException("Спутник с именем " + name + " не найден"));
	}

	@Override
	@Transactional
	public Satellite save(Satellite satellite) {
		if (satellite.getId() == null) {
			if (repository.existsByName(satellite.getName())) {
				throw new IllegalArgumentException("Спутник с именем " + satellite.getName() + " уже существует");
			}
		} else {
			Satellite existing = repository.findById(satellite.getId())
					.orElseThrow(() -> new EntityNotFoundException("Спутник с id " + satellite.getId() + " не найден"));

			if (!existing.getName().equals(satellite.getName()) && repository.existsByName(satellite.getName())) {
				throw new IllegalArgumentException("Спутник с именем " + satellite.getName() + " уже существует");
			}
		}
		log.info("Сохранение спутника: {} типа {}", satellite.getName(), satellite.getType());
		return repository.save(satellite);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		if (!repository.existsById(id)) {
			throw new EntityNotFoundException("Спутник с id " + id + " не найден");
		}
		repository.deleteById(id);
		log.info("Удален спутник с id: {}", id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Satellite> findByConstellationId(Long constellationId) {
		return repository.findByConstellationId(constellationId);
	}
}
