package ru.bmstu.analysis.service;

import ru.bmstu.analysis.controller.request.AnalyzeRequest;
import ru.bmstu.analysis.controller.response.AnalysisReportDto;

import java.util.List;
import java.util.UUID;

public interface AnalysisService {

    void analyzeAndStore(AnalyzeRequest request);

    List<AnalysisReportDto> getReportsByWorkId(UUID workId);
}
