package com.sooum.data.tag.repository;

import com.sooum.data.tag.entity.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {

    @Query("SELECT t FROM Tag t " +
            "WHERE t.isActive = true " +
            "AND LOWER(t.content) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "ORDER BY t.count DESC")
    List<Tag> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select t from Tag t where t.content in :tagContents")
    List<Tag> findTagList(@Param("tagContents") List<String> tagContents);

    @Query("select t " +
            "from Tag t " +
            "where t not in :excludeTags " +
            "   and (t.count > 0) " +
            "   and t.isActive = true " +
            "   and t.content != '19금' " +
            "order by t.count desc")
    List<Tag> findRecommendTagList(@Param("excludeTags") List<Tag> excludeTags, Pageable pageable);

    @Modifying
    @Transactional
    @Query("update Tag t set t.count = t.count + 1 where t in :tags")
    void incrementTagCount(@Param("tags") List<Tag> tags);
}
