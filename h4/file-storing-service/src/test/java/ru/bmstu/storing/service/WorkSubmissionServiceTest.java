package ru.bmstu.storing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import io.minio.MinioClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.bmstu.storing.FileStoringServiceApplication;
import ru.bmstu.storing.client.AnalysisServiceClient;
import ru.bmstu.storing.config.MinioConfig;
import ru.bmstu.storing.controller.request.SubmitWorkRequest;
import ru.bmstu.storing.controller.response.SubmitWorkResponse;
import ru.bmstu.storing.entity.SubmissionStatus;

@SpringBootTest(
    classes = {
        FileStoringServiceApplication.class,
        WorkSubmissionServiceTest.OverrideConfig.class,
    }
)
class WorkSubmissionServiceTest {

    @Autowired
    private WorkSubmissionService service;

    @MockitoBean
    private AnalysisServiceClient analysisServiceClient;

    @TestConfiguration
    static class OverrideConfig {

        @Bean
        @Primary
        MinioClient minioClient() {
            return mock(MinioClient.class);
        }

        @Bean
        @Primary
        MinioConfig minioConfig() {
            return new MinioConfig() {
                @Override
                public String getBucket() {
                    return "test-bucket";
                }
            };
        }
    }

    @Test
    void shouldRejectFileOverLimit() {
        SubmitWorkRequest request = new SubmitWorkRequest();
        request.setStudentName("student");
        request.setFileName("big.pdf");
        request.setFileSize(2_000_000L);
        request.setContentType("application/pdf");

        SubmitWorkResponse response = service.submitWork(request);

        assertThat(response.status()).isEqualTo(SubmissionStatus.NEEDS_REVISION.name());
        assertThat(response.message()).contains("exceeds limit");
        assertThat(response.workId()).isNull();
    }

    @Test
    void shouldAcceptValidFile() {
        SubmitWorkRequest request = new SubmitWorkRequest();
        request.setStudentName("student");
        request.setFileName("work.pdf");
        request.setFileSize(500_000L);
        request.setContentType("application/pdf");
        request.setFileContent(new byte[]{1, 2, 3});

        SubmitWorkResponse response = service.submitWork(request);

        assertThat(response.status()).isEqualTo(SubmissionStatus.SUBMITTED.name());
        assertThat(response.workId()).isNotNull();
    }
}
