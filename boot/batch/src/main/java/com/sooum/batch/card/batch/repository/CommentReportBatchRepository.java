package com.sooum.batch.card.batch.repository;

import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.report.entity.CommentReport;
import com.sooum.data.report.repository.CommentReportRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentReportBatchRepository extends CommentReportRepository {
    @Query("select cr from CommentReport cr where cr.targetCard in :targetCardList")
    List<CommentReport> findCommentReportsForDeletion(@Param("targetCardList")List<CommentCard> targetCardList);
}
