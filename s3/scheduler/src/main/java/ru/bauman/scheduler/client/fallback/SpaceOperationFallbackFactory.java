package ru.bauman.scheduler.client.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.bauman.scheduler.client.SpaceOperationClient;

@Slf4j
@Component
public class SpaceOperationFallbackFactory implements FallbackFactory<SpaceOperationClient> {
    @Override
    public SpaceOperationClient create(Throwable cause) {
        return request -> {
            log.error("Fallback: не удалось выполнить миссию {}: {}",
                    request, cause.getMessage(), cause);
            return ResponseEntity.status(503).build();
        };
    }
}