package com.sooum.core.domain.tag.repository;

import com.sooum.core.domain.tag.entity.CommentTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentTagRepository extends JpaRepository<CommentTag, Long> {

    List<CommentTag> findAllByCommentCard_Pk(Long cardPk);
}
