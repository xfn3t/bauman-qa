package ru.bmstu.storing.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.bmstu.storing.service.dto.WorkDto;

import java.util.Map;

@FeignClient(name = "file-analysis-service", url = "${analysis.service.url}")
public interface AnalysisServiceClient {

    @PostMapping("/api/internal/analyze")
    Map<String, Object> triggerAnalysis(@RequestBody WorkDto workDto);
}
