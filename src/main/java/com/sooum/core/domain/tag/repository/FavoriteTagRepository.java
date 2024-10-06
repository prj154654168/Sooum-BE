package com.sooum.core.domain.tag.repository;

import com.sooum.core.domain.tag.entity.FavoriteTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteTagRepository extends JpaRepository<FavoriteTag, Long> {
    boolean existsByTag_ContentAndMember_Pk(String tagContent, Long memberPk);
    Optional<FavoriteTag> findByTag_ContentAndMember_Pk(String tagContent, Long memberPk);
}
