package ru.bauman.scheduler.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.bauman.scheduler.client.fallback.SpaceOperationFallbackFactory;
import ru.bauman.scheduler.config.FeignConfig;
import ru.bauman.scheduler.dto.MissionRequest;

@FeignClient(
        name = "space-operation",
        url = "${app.space-center-service.url}",
        configuration = FeignConfig.class,
        fallbackFactory = SpaceOperationFallbackFactory.class
)
public interface SpaceOperationClient {
    @PostMapping("/missions")
    ResponseEntity<Void> executeMission(@RequestBody MissionRequest request);
}