package ru.bmstu.analysis.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI analysisOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("File Analysis Service")
                        .description("Анализ формата и размера файлов")
                        .version("1.0"));
    }
}
