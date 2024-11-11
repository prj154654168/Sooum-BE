package com.sooum.data.img.repository;

import com.sooum.data.img.entity.CardImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CardImgRepository extends JpaRepository<CardImg, Long> {
    @Modifying
    @Query("delete from CardImg u where u.imgName = :imgName")
    void deleteByImgName(@Param("imgName") String imgName);

    Optional<CardImg> findByImgName(String imgName);
}
