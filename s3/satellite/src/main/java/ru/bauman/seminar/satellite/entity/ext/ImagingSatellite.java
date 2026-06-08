package ru.bauman.seminar.satellite.entity.ext;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import ru.bauman.seminar.satellite.entity.Satellite;
import ru.bauman.seminar.satellite.entity.SatelliteState;
import ru.bauman.seminar.satellite.entity.SatelliteType;

import java.math.BigDecimal;

@Entity
@Table(name = "imaging_satellites")
@DiscriminatorValue("IMAGING")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Slf4j
public class ImagingSatellite extends Satellite {

	private static final BigDecimal ENERGY_CONSUMPTION = new BigDecimal("0.08");

	@Column(name = "resolution")
	private BigDecimal resolution;

	@Builder.Default
	@Column(name = "photos_taken")
	private Integer photosTaken = 0;

	@Override
	public SatelliteType getType() {
		return SatelliteType.IMAGING;
	}

	@Override
	public void performMission() {
		if (getState() == SatelliteState.ACTIVE) {
			photosTaken++;
			log.info("📸 {}: Съемка, разрешение {} м/пиксель, снимков: {}",
					getName(), resolution, photosTaken);
			consumeBattery(ENERGY_CONSUMPTION);
		} else {
			log.info("🛑 {}: Неактивен, миссия невозможна (состояние: {})", getName(), getState());
		}
	}
}