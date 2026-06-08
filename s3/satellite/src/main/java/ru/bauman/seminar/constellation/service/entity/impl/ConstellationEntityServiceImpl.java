package ru.bauman.seminar.constellation.service.entity.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bauman.seminar.common.exception.EntityNotFoundException;
import ru.bauman.seminar.constellation.entity.Constellation;
import ru.bauman.seminar.constellation.repository.ConstellationRepository;
import ru.bauman.seminar.constellation.service.entity.ConstellationEntityService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConstellationEntityServiceImpl implements ConstellationEntityService {

	private final ConstellationRepository repository;

	@Override
	@Transactional(readOnly = true)
	public List<Constellation> findAll() {
		return repository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Constellation findById(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Группировка с id " + id + " не найдена"));
	}

	@Override
	@Transactional(readOnly = true)
	public Constellation findByName(String name) {
		return repository.findByName(name)
				.orElseThrow(() -> new EntityNotFoundException("Группировка с именем " + name + " не найдена"));
	}

	@Override
	@Transactional(readOnly = true)
	public List<Constellation> findAllWithSatellites() {
		return repository.findAllWithSatellites();
	}

	@Override
	@Transactional(readOnly = true)
	public Constellation findByIdWithSatellites(Long id) {
		return repository.findByIdWithSatellites(id)
				.orElseThrow(() -> new EntityNotFoundException("Группировка с id " + id + " не найдена"));
	}


	@Override
	@Transactional
	public Constellation save(Constellation entity) {
		if (entity.getId() == null && repository.existsByName(entity.getName())) {
			throw new IllegalArgumentException("Группировка с именем " + entity.getName() + " уже существует");
		}
		if (entity.getId() != null) {
			Constellation existing = findById(entity.getId());
			if (!existing.getName().equals(entity.getName()) && repository.existsByName(entity.getName())) {
				throw new IllegalArgumentException("Группировка с именем " + entity.getName() + " уже существует");
			}
		}
		Constellation saved = repository.save(entity);
		log.info("Сохранена группировка: {}", saved.getName());
		return saved;
	}

	@Override
	@Transactional
	public void delete(Long id) {
		if (!repository.existsById(id)) {
			throw new EntityNotFoundException("Группировка с id " + id + " не найдена");
		}
		repository.deleteById(id);
		log.info("Удалена группировка с id: {}", id);
	}
}