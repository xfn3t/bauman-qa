package ru.bmstu.cosmoscan.tests.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.*;
import ru.bmstu.cosmoscan.tests.config.TestConfig;
import ru.bmstu.cosmoscan.tests.dto.AnalysisReportDto;
import ru.bmstu.cosmoscan.tests.dto.SubmitWorkRequest;
import ru.bmstu.cosmoscan.tests.dto.SubmitWorkResponse;

@Epic("КосмоСкан API")
@Feature("tests тестирование API-эндпоинтов")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CosmoscanApiTest {

    private static final String WORKS_PATH = "/api/v1/works";

    private static UUID createdWorkId;

    @BeforeAll
    static void setUp() {
        TestConfig.init();
    }

    @Test
    @Order(1)
    @DisplayName("POST /api/v1/works - успешная отправка работы")
    void submitWorkJsonShouldReturnOk() {
        var request = SubmitWorkRequest.of(
            "Иван Петров",
            "lab_report.pdf",
            2048L,
            "application/pdf",
            "dummy-content".getBytes(StandardCharsets.UTF_8)
        );

        SubmitWorkResponse response = given()
            .filter(new AllureRestAssured())
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post(WORKS_PATH)
            .then()
            .statusCode(200)
            .body("workId", notNullValue())
            .body(
                "status",
                anyOf(
                    equalTo("ACCEPTED"),
                    equalTo("SUBMITTED"),
                    equalTo("NEEDS_REVISION"),
                    equalTo("ERROR")
                )
            )
            .body("message", notNullValue())
            .extract()
            .as(SubmitWorkResponse.class);

        assertNotNull(response.workId(), "workId must not be null");
        assertNotNull(response.status(), "status must not be null");

        createdWorkId = response.workId();
        attachWorkId(response.workId());
    }

    @Test
    @Order(2)
    @DisplayName(
        "POST /api/v1/works (multipart) - успешная отправка работы с файлом"
    )
    void submitWorkMultipartShouldReturnOk() {
        Response response = given()
            .filter(new AllureRestAssured())
            .contentType(ContentType.MULTIPART)
            .multiPart("studentName", "Мария Смирнова")
            .multiPart(
                "file",
                "essay.txt",
                "Содержимое файла для проверки".getBytes(
                    StandardCharsets.UTF_8
                ),
                "text/plain"
            )
            .when()
            .post(WORKS_PATH)
            .then()
            .statusCode(200)
            .body("workId", notNullValue())
            .body(
                "status",
                anyOf(
                    equalTo("ACCEPTED"),
                    equalTo("SUBMITTED"),
                    equalTo("NEEDS_REVISION"),
                    equalTo("ERROR")
                )
            )
            .extract()
            .response();

        SubmitWorkResponse parsed = response.as(SubmitWorkResponse.class);
        assertNotNull(parsed.workId(), "workId must not be null");
        assertNotNull(parsed.status(), "status must not be null");

        attachWorkId(parsed.workId());

        if (createdWorkId == null) {
            createdWorkId = parsed.workId();
        }
    }

    @Test
    @Order(3)
    @DisplayName(
        "GET /api/v1/works/{workId}/status - получение статуса существующей работы"
    )
    void getWorkStatusShouldReturnWorkData() {
        assertNotNull(
            createdWorkId,
            "createdWorkId must be set by a prior submit test"
        );

        given()
            .filter(new AllureRestAssured())
            .when()
            .get(WORKS_PATH + "/{workId}/status", createdWorkId)
            .then()
            .statusCode(200)
            .body("workId", equalTo(createdWorkId.toString()))
            .body("fileName", notNullValue())
            .body("fileSize", notNullValue())
            .body("contentType", notNullValue());
    }

    @Test
    @Order(4)
    @DisplayName(
        "GET /api/v1/works/{workId}/status - несуществующая работа возвращает 404"
    )
    void getWorkStatusNotFoundShouldReturn404() {
        given()
            .filter(new AllureRestAssured())
            .when()
            .get(WORKS_PATH + "/{workId}/status", UUID.randomUUID())
            .then()
            .statusCode(404);
    }

    @Test
    @Order(5)
    @DisplayName(
        "GET /api/v1/works/{workId}/reports - получение отчетов по работе"
    )
    void getReportsShouldReturnReportList() {
        assertNotNull(
            createdWorkId,
            "createdWorkId must be set by a prior submit test"
        );

        Response response = given()
            .filter(new AllureRestAssured())
            .when()
            .get(WORKS_PATH + "/{workId}/reports", createdWorkId)
            .then()
            .extract()
            .response();

        int statusCode = response.statusCode();
        assertTrue(
            statusCode == 200 || statusCode == 404,
            "Expected 200 or 404, got: " + statusCode
        );

        if (statusCode == 200) {
            List<AnalysisReportDto> reports = response
                .jsonPath()
                .getList("", AnalysisReportDto.class);
            assertNotNull(reports, "Reports list must not be null");
        }
    }

    @Test
    @Order(6)
    @DisplayName(
        "GET /api/v1/works/{workId}/reports - невалидный UUID возвращает 4xx"
    )
    void getReportsInvalidUuidShouldReturn4xx() {
        int statusCode = given()
            .filter(new AllureRestAssured())
            .when()
            .get(WORKS_PATH + "/{workId}/reports", "not-a-uuid")
            .then()
            .extract()
            .statusCode();

        assertTrue(
            statusCode >= 400 && statusCode < 500,
            "Expected 4xx status for invalid UUID, got: " + statusCode
        );
    }

    @Step("Получен workId = {workId}")
    private void attachWorkId(UUID workId) {}
}
