package com.sooum.data.report.repository;

import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.report.entity.CommentReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {

    @Query("select cr from CommentReport cr where cr.targetCard.pk = :cardPk")
    List<CommentReport> findByTargetCard_Pk(@Param("cardPk") Long cardPk);

    boolean existsByReporter_PkAndTargetCard_Pk(Long cardPk, Long memberPk);

    @Modifying
    @Transactional
    @Query("delete from CommentReport c where c.targetCard = :cardPk")
    void deleteAllByCommentCardPk(@Param("cardPk") CommentCard cardPk);

    @Modifying
    @Transactional
    @Query("delete from CommentReport c where c.reporter.pk = :memberPk or c.targetCard.writer.pk = :memberPk")
    void deleteAllCommentReports(@Param("memberPk") Long memberPk);
}
