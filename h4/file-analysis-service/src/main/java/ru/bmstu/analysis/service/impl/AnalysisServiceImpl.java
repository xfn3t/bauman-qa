package ru.bmstu.analysis.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bmstu.analysis.controller.request.AnalyzeRequest;
import ru.bmstu.analysis.controller.response.AnalysisReportDto;
import ru.bmstu.analysis.entity.AnalysisReport;
import ru.bmstu.analysis.entity.ReportStatus;
import ru.bmstu.analysis.repository.AnalysisReportRepository;
import ru.bmstu.analysis.service.AnalysisService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {

    private static final long MAX_FILE_SIZE = 1_048_576L;
    private static final List<String> ALLOWED_EXTENSIONS = List.of("pdf", "docx", "txt");
    private static final List<String> ALLOWED_MIME_TYPES = List.of(
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "text/plain"
    );

    private final AnalysisReportRepository reportRepository;

    @Override
    @Transactional
    public void analyzeAndStore(AnalyzeRequest request) {
        log.info("Analyzing work {}", request.workId());

        AnalysisReport report = new AnalysisReport();
        report.setWorkId(request.workId());
        report.setFileName(request.fileName());
        report.setFileSize(request.fileSize());

        String extension = extractExtension(request.fileName());
        report.setFormat(extension != null ? extension : "unknown");

        List<String> issues = new ArrayList<>();
        checkFormat(extension, request.contentType(), issues);
        checkFileSize(request.fileSize(), issues);

        if (issues.isEmpty()) {
            report.setStatus(ReportStatus.ACCEPTED);
            report.setIssues("No issues found.");
        } else {
            report.setStatus(ReportStatus.NEEDS_REVISION);
            report.setIssues(String.join("; ", issues));
        }

        reportRepository.save(report);
        log.info("Analysis report {} created for work {}: status={}",
                report.getId(), request.workId(), report.getStatus());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnalysisReportDto> getReportsByWorkId(UUID workId) {
        return reportRepository.findByWorkIdOrderByCreatedAtDesc(workId)
                .stream()
                .map(r -> new AnalysisReportDto(
                        r.getId(),
                        r.getWorkId(),
                        r.getFileName(),
                        r.getFileSize(),
                        r.getFormat(),
                        r.getStatus().name(),
                        r.getIssues(),
                        r.getCreatedAt()
                ))
                .toList();
    }

    private void checkFormat(String extension, String contentType, List<String> issues) {
        if (extension == null || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            issues.add("Unsupported file format: '%s'. Allowed: %s"
                    .formatted(extension, String.join(", ", ALLOWED_EXTENSIONS)));
        }
        if (contentType != null && !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            issues.add("MIME type '%s' is not in the allowed list".formatted(contentType));
        }
    }

    private void checkFileSize(Long fileSize, List<String> issues) {
        if (fileSize != null && fileSize > MAX_FILE_SIZE) {
            issues.add("File size %d bytes exceeds maximum allowed %d bytes (%.2f MB)"
                    .formatted(fileSize, MAX_FILE_SIZE, fileSize / 1_048_576.0));
        }
    }

    private String extractExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return null;
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
}
