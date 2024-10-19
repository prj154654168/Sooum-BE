package com.sooum.core.domain.img.repository;

import com.sooum.core.domain.img.entity.UserUploadPic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ImgRepository extends JpaRepository<UserUploadPic, Long> {
    @Modifying
    @Query("delete from UserUploadPic u where u.imgName = :imgName")
    void deleteByImgName(@Param("imgName") String imgName);
}
