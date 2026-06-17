package ru.bmstu.cosmoscan.tests.config;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;

public final class TestConfig {

    public static final String BASE_URL = System.getProperty(
        "base.url",
        "http://localhost:8080"
    );

    private static volatile boolean initialized;

    private TestConfig() {}

    public static synchronized void init() {
        if (initialized) {
            return;
        }
        RestAssured.requestSpecification = new RequestSpecBuilder()
            .setBaseUri(BASE_URL)
            .addFilter(new RequestLoggingFilter())
            .addFilter(new ResponseLoggingFilter())
            .build();
        initialized = true;
    }
}
