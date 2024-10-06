package com.sooum.core.domain.tag.repository;

import com.sooum.core.domain.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    @Query("select t from Tag t where t.content in :tagContents")
    List<Tag> findTagList(@Param("tagContents") List<String> tagContents);
    Optional<Tag> findByContent(String content);
}
