package ru.bauman.seminar.satellite.service.entity;

import ru.bauman.seminar.common.service.FindByName;
import ru.bauman.seminar.common.service.entity.EntityCrudService;
import ru.bauman.seminar.satellite.entity.Satellite;

import java.util.List;

public interface SatelliteEntityService extends
		EntityCrudService<Satellite, Long>,
		FindByName<Satellite> {

	List<Satellite> findByConstellationId(Long constellationId);
}