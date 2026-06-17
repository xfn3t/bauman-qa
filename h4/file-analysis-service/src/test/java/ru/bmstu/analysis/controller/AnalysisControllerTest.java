package ru.bmstu.analysis.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.bmstu.analysis.controller.request.AnalyzeRequest;
import ru.bmstu.analysis.service.AnalysisService;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnalysisController.class)
class AnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AnalysisService analysisService;

    @Test
    void shouldTriggerAnalysis() throws Exception {
        AnalyzeRequest request = new AnalyzeRequest(
                UUID.randomUUID(), "student", "file.pdf", 1000L, "application/pdf", "s3/key"
        );

        doNothing().when(analysisService).analyzeAndStore(request);

        mockMvc.perform(post("/api/internal/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ANALYSIS_COMPLETED"));
    }

    @Test
    void shouldReturnReports() throws Exception {
        UUID workId = UUID.randomUUID();
        when(analysisService.getReportsByWorkId(workId)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/works/{workId}/reports", workId))
                .andExpect(status().isOk());
    }
}
