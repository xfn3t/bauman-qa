package ru.bauman.seminar.satellite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bauman.seminar.satellite.entity.Satellite;

import java.util.List;
import java.util.Optional;

@Repository
public interface SatelliteRepository extends JpaRepository<Satellite, Long> {
	Optional<Satellite> findByName(String name);
	List<Satellite> findByConstellationId(Long constellationId);
	boolean existsByName(String name);
}