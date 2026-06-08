package ru.bauman.scheduler.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import java.util.List;

@Validated
@ConfigurationProperties(prefix = "app.space-center-service")
public record MissionSchedulerProperties(
        @NotBlank String url,
        @NotNull List<@Valid MissionConfig> missions
) {
    public record MissionConfig(
            @NotNull TargetType targetType,
            @NotBlank String constellationName,
            String satelliteName,
            @NotBlank String cron
    ) {
        public enum TargetType { CONSTELLATION, SINGLE_SATELLITE }

        public boolean isValid() {
            if (targetType == TargetType.SINGLE_SATELLITE) {
                return satelliteName != null && !satelliteName.isBlank();
            }
            return true;
        }
    }
}