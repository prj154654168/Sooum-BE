package com.sooum.core.domain.notice.repository;

import com.sooum.core.domain.notice.entity.Notice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    @Query("select n from Notice n where n.pk < :lastPk order by n.pk desc ")
    List<Notice> findNotice(@Param("lastPk") Long lastPk, Pageable pageable);
}
