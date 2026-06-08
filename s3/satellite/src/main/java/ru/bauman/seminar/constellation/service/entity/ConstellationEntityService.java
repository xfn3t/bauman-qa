package ru.bauman.seminar.constellation.service.entity;


import ru.bauman.seminar.common.service.FindByName;
import ru.bauman.seminar.common.service.entity.EntityCrudService;
import ru.bauman.seminar.constellation.entity.Constellation;

import java.util.List;

public interface ConstellationEntityService extends EntityCrudService<Constellation, Long>, FindByName<Constellation> {
	List<Constellation> findAllWithSatellites();
	Constellation findByIdWithSatellites(Long id);
}