package com.sooum.core.domain.report.repository;

import com.sooum.core.domain.report.entity.CommentReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {
    List<CommentReport> findByTargetCard_Pk(Long cardPk);

    boolean existsByReporter_PkAndTargetCard_Pk(Long cardPk, Long memberPk);
}
