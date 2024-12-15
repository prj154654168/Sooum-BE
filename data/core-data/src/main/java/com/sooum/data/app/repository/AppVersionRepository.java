package com.sooum.data.app.repository;

import com.sooum.data.app.entity.AppVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AppVersionRepository extends JpaRepository<AppVersion, Long> {
    @Query("select a.latestVersion from AppVersion a where a.deviceType = com.sooum.data.member.entity.devicetype.DeviceType.ANDROID")
    String findAndroidLatestVersion();

    @Query("select a.latestVersion from AppVersion a where a.deviceType = com.sooum.data.member.entity.devicetype.DeviceType.IOS")
    String findIosLatestVersion();

}
