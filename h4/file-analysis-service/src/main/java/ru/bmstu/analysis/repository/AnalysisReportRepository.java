package ru.bmstu.analysis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bmstu.analysis.entity.AnalysisReport;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnalysisReportRepository extends JpaRepository<AnalysisReport, UUID> {
    List<AnalysisReport> findByWorkIdOrderByCreatedAtDesc(UUID workId);
}
