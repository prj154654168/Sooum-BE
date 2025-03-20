package com.sooum.data.notification.repository;

import com.sooum.data.notification.entity.FcmSchedulerContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FcmSchedulerContentRepository extends JpaRepository<FcmSchedulerContent, Long> {

    Optional<FcmSchedulerContent> findByPk(Long id);
}
