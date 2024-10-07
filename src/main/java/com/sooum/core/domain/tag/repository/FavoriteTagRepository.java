package com.sooum.core.domain.tag.repository;

import com.sooum.core.domain.tag.entity.FavoriteTag;
import com.sooum.core.domain.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface FavoriteTagRepository extends JpaRepository<FavoriteTag, Long> {
    boolean existsByTag_ContentAndMember_Pk(String tagContent, Long memberPk);
    Optional<FavoriteTag> findByTag_ContentAndMember_Pk(String tagContent, Long memberPk);

    @Query("select ft.tag from FavoriteTag ft where ft.member.pk = :memberPk")
    List<Tag> findFavoriteTag(@Param("memberPk") Long memberPk);
}