package com.sooum.data.tag.repository;

import com.sooum.data.tag.entity.FavoriteTag;
import com.sooum.data.tag.entity.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FavoriteTagRepository extends JpaRepository<FavoriteTag, Long> {
    boolean existsByTag_PkAndMember_Pk(Long tagPk, Long memberPk);
    Optional<FavoriteTag> findByTag_PkAndMember_Pk(Long tagPk, Long memberPk);

    @Query("select ft.tag from FavoriteTag ft where ft.member.pk = :memberPk")
    List<Tag> findFavoriteTag(@Param("memberPk") Long memberPk);

    @Query("select ft.tag.pk from FavoriteTag ft " +
            "where ft.member.pk = :memberPk and (:lastTagPk is null or ft.tag.pk < :lastTagPk)" +
            "order by ft.pk desc")
    List<Long> findMyFavoriteTags(@Param("memberPk") Long memberPk, @Param("lastTagPk") Long lastTagPk, Pageable pageable);

    @Modifying
    @Query("delete from FavoriteTag ft where ft.member.pk = :memberPk")
    void deleteAllFavoriteTag(@Param("memberPk") Long memberPk);
}