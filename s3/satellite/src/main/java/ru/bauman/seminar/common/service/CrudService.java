package ru.bauman.seminar.common.service;

import java.util.List;

public interface CrudService<REQUEST, RESPONSE, ID> {
	List<RESPONSE> findAll();
	RESPONSE findById(ID id);
	RESPONSE create(REQUEST request);
	RESPONSE update(ID id, REQUEST request);
	void delete(ID id);
}