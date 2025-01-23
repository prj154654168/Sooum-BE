package com.sooum.data.app.repository;

import com.sooum.data.app.entity.AppVersionFlag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AppVersionFlagRepository extends JpaRepository<AppVersionFlag, Long> {
    @Query("select af.isFlag from AppVersionFlag af")
    boolean findFlag();
}
