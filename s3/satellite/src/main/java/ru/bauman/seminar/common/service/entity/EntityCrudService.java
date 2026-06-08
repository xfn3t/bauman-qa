package ru.bauman.seminar.common.service.entity;

import java.util.List;

public interface EntityCrudService<T, ID> {
	List<T> findAll();
	T findById(ID id);
	T save(T entity);
	void delete(ID id);
}