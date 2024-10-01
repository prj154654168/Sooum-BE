package com.sooum.core.domain.report.repository;

import com.sooum.core.domain.report.entity.CommentReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {

    @Query("select cr from CommentReport cr where cr.targetCard.pk = :cardPk")
    List<CommentReport> findByTargetCard_Pk(@Param("cardPk") Long cardPk);

    boolean existsByReporter_PkAndTargetCard_Pk(Long cardPk, Long memberPk);
}
