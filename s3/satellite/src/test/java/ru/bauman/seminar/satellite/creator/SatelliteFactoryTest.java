package ru.bauman.seminar.satellite.creator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.bauman.seminar.satellite.creator.impl.CommunicationSatelliteFactory;
import ru.bauman.seminar.satellite.creator.impl.ImagingSatelliteFactory;
import ru.bauman.seminar.satellite.creator.param.CommunicationSatelliteParam;
import ru.bauman.seminar.satellite.creator.param.ImagingSatelliteParam;
import ru.bauman.seminar.satellite.entity.SatelliteType;
import ru.bauman.seminar.satellite.entity.ext.CommunicationSatellite;
import ru.bauman.seminar.satellite.entity.ext.ImagingSatellite;
import ru.bauman.seminar.satellite.exception.SpaceOperationException;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Тесты фабрик спутников")
class SatelliteFactoryTest {

	private final CommunicationSatelliteFactory commFactory = new CommunicationSatelliteFactory();
	private final ImagingSatelliteFactory imgFactory = new ImagingSatelliteFactory();


	@Test
	@DisplayName("CommunicationSatelliteFactory поддерживает тип COMMUNICATION")
	void commFactory_SupportsCommType() {
		assertThat(commFactory.isSatelliteTypeSupported(SatelliteType.COMMUNICATION)).isTrue();
		assertThat(commFactory.isSatelliteTypeSupported(SatelliteType.IMAGING)).isFalse();
	}

	@Test
	@DisplayName("ImagingSatelliteFactory поддерживает тип IMAGING")
	void imgFactory_SupportsImagingType() {
		assertThat(imgFactory.isSatelliteTypeSupported(SatelliteType.IMAGING)).isTrue();
		assertThat(imgFactory.isSatelliteTypeSupported(SatelliteType.COMMUNICATION)).isFalse();
	}


	@Test
	@DisplayName("CommunicationSatelliteFactory создает спутник с заданной пропускной способностью")
	void commFactory_CreatesSatelliteWithBandwidth() {
		var param = new CommunicationSatelliteParam("Связь-1", new BigDecimal("0.85"), new BigDecimal("500.0"));
		var satellite = commFactory.createSatelliteWithParameter(param);

		assertThat(satellite).isInstanceOf(CommunicationSatellite.class);
		CommunicationSatellite comm = (CommunicationSatellite) satellite;
		assertThat(comm.getName()).isEqualTo("Связь-1");
		assertThat(comm.getBatteryLevel()).isEqualByComparingTo("0.85");
		assertThat(comm.getBandwidth()).isEqualByComparingTo("500.0");
	}

	@Test
	@DisplayName("ImagingSatelliteFactory создает спутник с заданным разрешением")
	void imgFactory_CreatesSatelliteWithResolution() {
		var param = new ImagingSatelliteParam("ДЗЗ-1", new BigDecimal("0.92"), new BigDecimal("2.5"));
		var satellite = imgFactory.createSatelliteWithParameter(param);

		assertThat(satellite).isInstanceOf(ImagingSatellite.class);
		ImagingSatellite img = (ImagingSatellite) satellite;
		assertThat(img.getName()).isEqualTo("ДЗЗ-1");
		assertThat(img.getBatteryLevel()).isEqualByComparingTo("0.92");
		assertThat(img.getResolution()).isEqualByComparingTo("2.5");
		assertThat(img.getPhotosTaken()).isZero();
	}

	@Test
	@DisplayName("CommunicationSatelliteFactory бросает исключение при неверном типе параметра")
	void commFactory_ThrowsOnWrongParam() {
		var wrongParam = new ImagingSatelliteParam("ДЗЗ-X", new BigDecimal("0.5"), new BigDecimal("1.0"));
		assertThatThrownBy(() -> commFactory.createSatelliteWithParameter(wrongParam))
				.isInstanceOf(SpaceOperationException.class);
	}

	@Test
	@DisplayName("ImagingSatelliteFactory бросает исключение при неверном типе параметра")
	void imgFactory_ThrowsOnWrongParam() {
		var wrongParam = new CommunicationSatelliteParam("Связь-X", new BigDecimal("0.5"), new BigDecimal("100.0"));
		assertThatThrownBy(() -> imgFactory.createSatelliteWithParameter(wrongParam))
				.isInstanceOf(SpaceOperationException.class);
	}
}
