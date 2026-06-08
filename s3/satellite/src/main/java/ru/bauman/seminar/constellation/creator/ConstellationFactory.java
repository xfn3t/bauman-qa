package ru.bauman.seminar.constellation.creator;

import ru.bauman.seminar.constellation.entity.Constellation;

public interface ConstellationFactory {
	Constellation createConstellation(String name, String description);
	Constellation createDefaultConstellation();
	Constellation createConstellationWithDefaultSatellites(String name, String description);
}
