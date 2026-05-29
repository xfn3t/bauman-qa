package ru.bmstu.analysis.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.bmstu.analysis.controller.request.AnalyzeRequest;
import ru.bmstu.analysis.controller.response.AnalysisReportDto;
import ru.bmstu.analysis.service.impl.AnalysisServiceImpl;

@DataJpaTest
@Import(AnalysisServiceImpl.class)
class AnalysisServiceTest {

    @Autowired
    private AnalysisService analysisService;

    private UUID workId;

    @BeforeEach
    void setUp() {
        workId = UUID.randomUUID();
    }

    @Test
    void shouldAcceptPdfUnderLimit() {
        AnalyzeRequest request = new AnalyzeRequest(
            workId,
            "student",
            "work.pdf",
            500_000L,
            "application/pdf",
            "s3/key"
        );

        analysisService.analyzeAndStore(request);

        List<AnalysisReportDto> reports = analysisService.getReportsByWorkId(
            workId
        );
        assertThat(reports).hasSize(1);
        assertThat(reports.get(0).status()).isEqualTo("ACCEPTED");
        assertThat(reports.get(0).format()).isEqualTo("pdf");
    }

    @Test
    void shouldRejectZipFormat() {
        AnalyzeRequest request = new AnalyzeRequest(
            workId,
            "student",
            "archive.zip",
            500_000L,
            "application/zip",
            "s3/key"
        );

        analysisService.analyzeAndStore(request);

        List<AnalysisReportDto> reports = analysisService.getReportsByWorkId(workId);
        
        assertThat(reports).hasSize(1);
        assertThat(reports.get(0).status()).isEqualTo("NEEDS_REVISION");
        assertThat(reports.get(0).issues()).contains("Unsupported file format");
    }

    @Test
    void shouldRejectFileOverLimit() {
        AnalyzeRequest request = new AnalyzeRequest(
            workId,
            "student",
            "big.pdf",
            2_000_000L,
            "application/pdf",
            "s3/key"
        );

        analysisService.analyzeAndStore(request);

        List<AnalysisReportDto> reports = analysisService.getReportsByWorkId(
            workId
        );
        assertThat(reports).hasSize(1);
        assertThat(reports.get(0).status()).isEqualTo("NEEDS_REVISION");
        assertThat(reports.get(0).issues()).contains("exceeds maximum allowed");
    }

    @Test
    void shouldAcceptDocx() {
        AnalyzeRequest request = new AnalyzeRequest(
            workId,
            "student",
            "work.docx",
            100_000L,
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "s3/key"
        );

        analysisService.analyzeAndStore(request);

        List<AnalysisReportDto> reports = analysisService.getReportsByWorkId(
            workId
        );
        assertThat(reports).hasSize(1);
        assertThat(reports.get(0).status()).isEqualTo("ACCEPTED");
        assertThat(reports.get(0).format()).isEqualTo("docx");
    }

    @Test
    void shouldRejectUnknownExtension() {
        AnalyzeRequest request = new AnalyzeRequest(
            workId,
            "student",
            "file.xyz",
            100_000L,
            "application/octet-stream",
            "s3/key"
        );

        analysisService.analyzeAndStore(request);

        List<AnalysisReportDto> reports = analysisService.getReportsByWorkId(
            workId
        );
        assertThat(reports).hasSize(1);
        assertThat(reports.get(0).status()).isEqualTo("NEEDS_REVISION");
        assertThat(reports.get(0).issues()).contains("Unsupported file format");
    }
}
