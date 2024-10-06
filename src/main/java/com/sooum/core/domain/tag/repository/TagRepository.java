package com.sooum.core.domain.tag.repository;

import com.sooum.core.domain.tag.entity.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
    @Query("select t from Tag t where t.content in :tagContents")
    List<Tag> findTagList(@Param("tagContents") List<String> tagContents);

    @Query("select t from Tag t where t not in :excludeTags order by t.count")
    List<Tag> findRecommendTagList(@Param("excludeTags") List<Tag> excludeTags, Pageable pageable);
}
