package ru.bmstu.gateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI gatewayOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("КосмоСкан API Gateway")
                        .description("Единая точка входа в систему КосмоСкан")
                        .version("1.0"));
    }

    @Bean
    public GroupedOpenApi storingApi() {
        return GroupedOpenApi.builder()
                .group("file-storing-service")
                .pathsToMatch("/api/v1/works/**")
                .build();
    }

    @Bean
    public GroupedOpenApi analysisApi() {
        return GroupedOpenApi.builder()
                .group("file-analysis-service")
                .pathsToMatch("/api/v1/works/*/reports")
                .build();
    }
}
