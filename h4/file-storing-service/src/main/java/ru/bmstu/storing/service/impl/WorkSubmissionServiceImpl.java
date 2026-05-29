package ru.bmstu.storing.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bmstu.storing.config.MinioConfig;
import ru.bmstu.storing.controller.request.SubmitWorkRequest;
import ru.bmstu.storing.controller.response.SubmitWorkResponse;
import ru.bmstu.storing.entity.SubmissionStatus;
import ru.bmstu.storing.entity.WorkSubmission;
import ru.bmstu.storing.mapper.WorkSubmissionMapper;
import ru.bmstu.storing.outbox.OutboxEvent;
import ru.bmstu.storing.outbox.OutboxEventRepository;
import ru.bmstu.storing.outbox.OutboxEventType;
import ru.bmstu.storing.repository.WorkSubmissionRepository;
import ru.bmstu.storing.service.WorkSubmissionService;
import ru.bmstu.storing.service.dto.WorkDto;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkSubmissionServiceImpl implements WorkSubmissionService {

    private static final long MAX_FILE_SIZE = 1_048_576L;

    private final WorkSubmissionRepository repository;
    private final OutboxEventRepository outboxEventRepository;
    private final MinioClient minioClient;
    private final MinioConfig minioConfig;
    private final ObjectMapper objectMapper;
    private final WorkSubmissionMapper mapper;

    @Override
    @Transactional
    public SubmitWorkResponse submitWork(SubmitWorkRequest request) {
        if (request.getFileSize() != null && request.getFileSize() > MAX_FILE_SIZE) {
            return new SubmitWorkResponse(
                    null,
                    SubmissionStatus.NEEDS_REVISION.name(),
                    "File size %d exceeds limit of %d bytes".formatted(request.getFileSize(), MAX_FILE_SIZE)
            );
        }

        WorkSubmission submission = new WorkSubmission();
        submission.setStudentName(request.getStudentName());
        submission.setFileName(request.getFileName());
        submission.setFileSize(request.getFileSize());
        submission.setContentType(request.getContentType());
        submission.setStatus(SubmissionStatus.SUBMITTED);
        submission.setAnalysisTriggered(false);

        WorkSubmission saved = repository.save(submission);

        try {
            String s3Key = "works/%s/%s".formatted(saved.getId(), request.getFileName());
            InputStream stream = request.getInputStream() != null
                    ? request.getInputStream()
                    : new ByteArrayInputStream(request.getFileContent());

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getBucket())
                            .object(s3Key)
                            .stream(stream, request.getFileSize(), -1)
                            .contentType(request.getContentType())
                            .build()
            );
            saved.setS3Key(s3Key);
            repository.save(saved);
            log.info("Work {} stored in MinIO with key {}", saved.getId(), s3Key);
        } catch (Exception e) {
            log.error("Failed to store work {} in MinIO: {}", saved.getId(), e.getMessage(), e);
            saved.setStatus(SubmissionStatus.ERROR);
            repository.save(saved);
        }

        try {
            String payload = objectMapper.writeValueAsString(Map.of(
                    "workId", saved.getId().toString(),
                    "fileName", saved.getFileName(),
                    "fileSize", saved.getFileSize(),
                    "contentType", saved.getContentType()
            ));

            OutboxEvent event = new OutboxEvent();
            event.setAggregateId(saved.getId());
            event.setEventType(OutboxEventType.WORK_SUBMITTED);
            event.setPayload(payload);
            event.setPublished(false);
            outboxEventRepository.save(event);

            saved.setAnalysisTriggered(true);
            repository.save(saved);
            log.info("Outbox event created for work {}", saved.getId());
        } catch (Exception e) {
            log.error("Failed to create outbox event for work {}: {}", saved.getId(), e.getMessage(), e);
        }

        return new SubmitWorkResponse(
                saved.getId(),
                saved.getStatus().name(),
                "Work submitted successfully"
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WorkDto> getWorkById(UUID workId) {
        return repository.findById(workId).map(mapper::toDto);
    }
}
