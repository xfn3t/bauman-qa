package ru.bauman.seminar.satellite.creator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.bauman.seminar.satellite.creator.param.SatelliteParam;
import ru.bauman.seminar.satellite.entity.Satellite;
import ru.bauman.seminar.satellite.exception.SpaceOperationException;

import java.util.List;


@Component
@RequiredArgsConstructor
public class SatelliteCreationService {

	private final List<SatelliteFactory> factories;

	public Satellite createSatellite(SatelliteParam param) {
		return factories.stream()
				.filter(f -> f.isSatelliteTypeSupported(param.getType()))
				.findFirst()
				.orElseThrow(() -> new SpaceOperationException(
						"Не найдена фабрика для типа спутника: " + param.getType()))
				.createSatelliteWithParameter(param);
	}
}
