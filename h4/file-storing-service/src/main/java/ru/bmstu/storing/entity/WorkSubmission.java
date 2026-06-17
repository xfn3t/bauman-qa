package ru.bmstu.storing.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "work_submission")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class WorkSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "student_name", nullable = false, length = 512)
    private String studentName;

    @Column(name = "file_name", nullable = false, length = 512)
    private String fileName;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "content_type", nullable = false, length = 255)
    private String contentType;

    @Column(name = "s3_key", length = 1024)
    private String s3Key;

    @Column(name = "submitted_at", nullable = false, updatable = false)
    private Instant submittedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private SubmissionStatus status;

    @Column(name = "analysis_triggered", nullable = false)
    private boolean analysisTriggered;

    @PrePersist
    protected void onCreate() {
        if (submittedAt == null) {
            submittedAt = Instant.now();
        }
        if (status == null) {
            status = SubmissionStatus.SUBMITTED;
        }
    }
}
