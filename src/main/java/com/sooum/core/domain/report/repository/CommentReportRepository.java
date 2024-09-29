package com.sooum.core.domain.report.repository;

import com.sooum.core.domain.card.entity.CommentCard;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.report.entity.CommentReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {
    boolean existsByReporterAndTargetCard(Member member, CommentCard card);

    List<CommentReport> findByTargetCard(CommentCard card);
}
