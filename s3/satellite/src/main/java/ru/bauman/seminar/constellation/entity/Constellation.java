package ru.bauman.seminar.constellation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.bauman.seminar.satellite.entity.Satellite;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "constellations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Constellation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String name;

	private String description;

	@OneToMany(mappedBy = "constellation", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Satellite> satellites = new ArrayList<>();

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	private LocalDateTime updatedAt;

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public void addSatellite(Satellite satellite) {
		satellites.add(satellite);
		satellite.setConstellation(this);
	}

	public void removeSatellite(Satellite satellite) {
		satellites.remove(satellite);
		satellite.setConstellation(null);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private String name;
		private String description;
		private final List<Satellite> satellites = new ArrayList<>();

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder description(String description) {
			this.description = description;
			return this;
		}

		public Builder addSatellite(Satellite satellite) {
			this.satellites.add(satellite);
			return this;
		}

		public Builder addSatellites(Satellite... satellites) {
			this.satellites.addAll(Arrays.asList(satellites));
			return this;
		}

		public Constellation build() {
			if (name == null || name.isBlank()) {
				throw new IllegalArgumentException("Имя группировки обязательно");
			}

			Constellation constellation = new Constellation();
			constellation.setName(name);
			constellation.setDescription(description);

			constellation.setSatellites(new ArrayList<>());

			for (Satellite satellite : satellites) {
				constellation.addSatellite(satellite);
			}

			return constellation;
		}
	}
}