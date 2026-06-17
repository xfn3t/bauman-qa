package ru.bmstu.storing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bmstu.storing.entity.WorkSubmission;

import java.util.UUID;

@Repository
public interface WorkSubmissionRepository extends JpaRepository<WorkSubmission, UUID> {
}
