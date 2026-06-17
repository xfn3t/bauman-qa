package ru.bmstu.cosmoscan.tests.tests;

import static io.restassured.RestAssured.given;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.restassured.AllureRestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.bmstu.cosmoscan.tests.config.TestConfig;

@Epic("КосмоСкан API")
@Feature("Проверка доступности сервисов")
class HealthCheckTest {

    @BeforeAll
    static void setUp() {
        TestConfig.init();
    }

    @Test
    @DisplayName("API Gateway доступен — TCP connect к порту 8080")
    @Description(
        "Проверка TCP-подключения к API Gateway. Если сервис недоступен, тест пропускается с предупреждением."
    )
    void apiGateway_shouldBeReachable_Tcp() {
        given()
            .filter(new AllureRestAssured())
            .when()
            .get("/webjars/swagger-ui/index.html")
            .then()
            .statusCode(200);
    }
}
