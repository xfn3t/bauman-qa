package ru.bauman.scheduler.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "mission_outbox")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MissionOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "deduplication_id", unique = true, nullable = false)
    private String deduplicationId;

    @Column(name = "constellation_name", nullable = false)
    private String constellationName;

    @Column(name = "satellite_name")
    private String satelliteName;

    @Column(name = "mission_type", nullable = false)
    private String missionType;

    @Column(name = "activate_before_mission")
    private boolean activateBeforeMission;

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    private int attempts;

    @Column(name = "last_error")
    private String lastError;

    @Column(name = "next_retry_at")
    private LocalDateTime nextRetryAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (nextRetryAt == null) nextRetryAt = createdAt;
        if (status == null) status = OutboxStatus.PENDING;
    }
}