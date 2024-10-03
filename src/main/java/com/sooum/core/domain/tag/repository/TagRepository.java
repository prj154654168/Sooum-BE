package com.sooum.core.domain.tag.repository;

import com.sooum.core.domain.tag.entity.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {

    @Query("SELECT t FROM Tag t " +
            "WHERE t.isActive = true " +
            "AND LOWER(t.content) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "ORDER BY t.count DESC")
    List<Tag> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
