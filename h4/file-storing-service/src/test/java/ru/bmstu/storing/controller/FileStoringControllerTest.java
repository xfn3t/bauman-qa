package ru.bmstu.storing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.bmstu.storing.controller.response.SubmitWorkResponse;
import ru.bmstu.storing.service.WorkSubmissionService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileStoringController.class)
class FileStoringControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorkSubmissionService workSubmissionService;

    @Test
    void shouldSubmitWork() throws Exception {
        UUID workId = UUID.randomUUID();
        when(workSubmissionService.submitWork(any()))
                .thenReturn(new SubmitWorkResponse(workId, "SUBMITTED", "ok"));

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.pdf", "application/pdf", "content".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/works")
                        .file(file)
                        .param("studentName", "Иванов")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workId").value(workId.toString()))
                .andExpect(jsonPath("$.status").value("SUBMITTED"));
    }
}
