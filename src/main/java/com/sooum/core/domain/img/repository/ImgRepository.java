package com.sooum.core.domain.img.repository;

import com.sooum.core.domain.img.entity.UserUploadPic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImgRepository extends JpaRepository<UserUploadPic, Long> {
}
