package ru.bauman.seminar.common;

import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestContainersConfig {

	private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(
			DockerImageName.parse("postgres:15-alpine")
	)
			.withDatabaseName("testdb")
			.withUsername("test")
			.withPassword("test");

	static {
		POSTGRES.start();
	}

	public static String getJdbcUrl() {
		return POSTGRES.getJdbcUrl();
	}

	public static String getUsername() {
		return POSTGRES.getUsername();
	}

	public static String getPassword() {
		return POSTGRES.getPassword();
	}
}