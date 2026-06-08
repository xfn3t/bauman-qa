package ru.bauman.scheduler.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface OutboxRepository extends JpaRepository<MissionOutbox, Long> {

    @Query("SELECT o FROM MissionOutbox o WHERE o.status = 'PENDING' AND o.nextRetryAt <= CURRENT_TIMESTAMP ORDER BY o.createdAt")
    List<MissionOutbox> findPendingMessages();
}