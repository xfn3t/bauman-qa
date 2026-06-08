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
@Table(name = "communication_satellites")
@DiscriminatorValue("COMMUNICATION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Slf4j
public class CommunicationSatellite extends Satellite {

	private static final BigDecimal ENERGY_CONSUMPTION = new BigDecimal("0.05");

	@Column(name = "bandwidth")
	private BigDecimal bandwidth;

	@Override
	public SatelliteType getType() {
		return SatelliteType.COMMUNICATION;
	}

	@Override
	public void performMission() {
		if (getState() == SatelliteState.ACTIVE) {
			log.info("📡 {}: Передача данных, скорость {} Мбит/с", getName(), bandwidth);
			consumeBattery(ENERGY_CONSUMPTION);
		} else {
			log.info("🛑 {}: Неактивен, миссия невозможна (состояние: {})", getName(), getState());
		}
	}
}