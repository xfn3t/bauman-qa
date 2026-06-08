package ru.bauman.seminar.constellation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bauman.seminar.constellation.entity.Constellation;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConstellationRepository extends JpaRepository<Constellation, Long> {
	Optional<Constellation> findByName(String name);
	boolean existsByName(String name);

	@Query("SELECT DISTINCT c FROM Constellation c LEFT JOIN FETCH c.satellites")
	List<Constellation> findAllWithSatellites();

	@Query("SELECT c FROM Constellation c LEFT JOIN FETCH c.satellites WHERE c.id = :id")
	Optional<Constellation> findByIdWithSatellites(@Param("id") Long id);
}