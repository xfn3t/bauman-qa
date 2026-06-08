package ru.bauman.seminar.common;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import ru.bauman.seminar.satellite.creator.impl.CommunicationSatelliteFactory;
import ru.bauman.seminar.satellite.creator.impl.ImagingSatelliteFactory;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({
		CommunicationSatelliteFactory.class,
		ImagingSatelliteFactory.class
})
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

	static final PostgreSQLContainer<?> POSTGRES;

	static {
		POSTGRES = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15-alpine"))
				.withDatabaseName("testdb")
				.withUsername("test")
				.withPassword("test");
		POSTGRES.start();
	}

	@DynamicPropertySource
	static void postgresProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
		registry.add("spring.datasource.username", POSTGRES::getUsername);
		registry.add("spring.datasource.password", POSTGRES::getPassword);
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
	}
}