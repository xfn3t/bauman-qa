package ru.bauman.seminar.satellite.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Embeddable
@Getter
@NoArgsConstructor
public class EnergySystem {

	private static final int SCALE = 2;

	@Column(name = "battery_level")
	private BigDecimal batteryLevel;

	@Column(name = "min_battery")
	private BigDecimal minBattery;

	@Column(name = "max_battery")
	private BigDecimal maxBattery;

	@Column(name = "low_battery_threshold")
	private BigDecimal lowBatteryThreshold;

	private EnergySystem(BigDecimal batteryLevel, BigDecimal minBattery,
						 BigDecimal maxBattery, BigDecimal lowBatteryThreshold) {
		this.batteryLevel = batteryLevel;
		this.minBattery = minBattery;
		this.maxBattery = maxBattery;
		this.lowBatteryThreshold = lowBatteryThreshold;
		validate();
	}

	private void validate() {
		if (minBattery == null || maxBattery == null || batteryLevel == null || lowBatteryThreshold == null) {
			throw new IllegalArgumentException("Все поля EnergySystem должны быть заданы");
		}
		if (minBattery.compareTo(maxBattery) > 0) {
			throw new IllegalArgumentException("minBattery не может быть больше maxBattery");
		}
		if (lowBatteryThreshold.compareTo(minBattery) < 0 || lowBatteryThreshold.compareTo(maxBattery) > 0) {
			throw new IllegalArgumentException("lowBatteryThreshold должен быть между minBattery и maxBattery");
		}
		if (batteryLevel.compareTo(minBattery) < 0 || batteryLevel.compareTo(maxBattery) > 0) {
			throw new IllegalArgumentException("batteryLevel должен быть между minBattery и maxBattery");
		}
	}

	public void consume(BigDecimal amount) {
		if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("Amount не может быть null или отрицательным");
		}
		batteryLevel = batteryLevel.subtract(amount)
				.max(minBattery)
				.setScale(SCALE, RoundingMode.HALF_UP);
	}

	public void recharge(BigDecimal amount) {
		if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("Amount не может быть null или отрицательным");
		}
		batteryLevel = batteryLevel.add(amount)
				.min(maxBattery)
				.setScale(SCALE, RoundingMode.HALF_UP);
	}

	public boolean hasSufficientCharge() {
		return batteryLevel.compareTo(lowBatteryThreshold) > 0;
	}

	public boolean isCritical() {
		return batteryLevel.compareTo(lowBatteryThreshold) <= 0;
	}

	public static EnergySystemBuilder builder() {
		return new EnergySystemBuilder();
	}

	public static class EnergySystemBuilder {
		private BigDecimal batteryLevel = BigDecimal.ONE;
		private BigDecimal minBattery = BigDecimal.ZERO;
		private BigDecimal maxBattery = BigDecimal.ONE;
		private BigDecimal lowBatteryThreshold = new BigDecimal("0.2");

		EnergySystemBuilder() {}

		public EnergySystemBuilder batteryLevel(BigDecimal batteryLevel) {
			this.batteryLevel = batteryLevel;
			return this;
		}

		public EnergySystemBuilder minBattery(BigDecimal minBattery) {
			this.minBattery = minBattery;
			return this;
		}

		public EnergySystemBuilder maxBattery(BigDecimal maxBattery) {
			this.maxBattery = maxBattery;
			return this;
		}

		public EnergySystemBuilder lowBatteryThreshold(BigDecimal lowBatteryThreshold) {
			this.lowBatteryThreshold = lowBatteryThreshold;
			return this;
		}

		public EnergySystem build() {
			return new EnergySystem(
					batteryLevel,
					minBattery,
					maxBattery,
					lowBatteryThreshold
			);
		}
	}

	@Override
	public String toString() {
		return String.format("EnergySystem{batteryLevel=%.2f, min=%.2f, max=%.2f, threshold=%.2f}",
				batteryLevel, minBattery, maxBattery, lowBatteryThreshold);
	}
}