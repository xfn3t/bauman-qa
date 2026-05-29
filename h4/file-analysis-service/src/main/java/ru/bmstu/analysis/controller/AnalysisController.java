package ru.bmstu.analysis.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.analysis.controller.request.AnalyzeRequest;
import ru.bmstu.analysis.controller.response.AnalysisReportDto;
import ru.bmstu.analysis.service.AnalysisService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    @PostMapping("/api/internal/analyze")
    public ResponseEntity<Map<String, String>> analyze(@RequestBody AnalyzeRequest request) {
        analysisService.analyzeAndStore(request);
        return ResponseEntity.ok(Map.of("status", "ANALYSIS_COMPLETED"));
    }

    @GetMapping("/api/v1/works/{workId}/reports")
    public ResponseEntity<List<AnalysisReportDto>> getReports(@PathVariable String workId) {
        UUID id = UUID.fromString(workId);
        List<AnalysisReportDto> reports = analysisService.getReportsByWorkId(id);
        return ResponseEntity.ok(reports);
    }
}
