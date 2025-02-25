package com.sooum.api.appversion.service;

import com.sooum.api.appversion.dto.AppVersionStatus;
import com.sooum.data.app.entity.AppVersion;
import com.sooum.data.app.repository.AppVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AppVersionService {
    private final AppVersionRepository appVersionRepository;

    public AppVersionStatus findAndroidVersionStatus(String clientVersion) {
        AppVersion androidVersionInfo = appVersionRepository.findAndroidVersionInfo();

        String minVersion = androidVersionInfo.getMinVersion();
        String latestVersion = androidVersionInfo.getLatestVersion();
        String pendingVersion = androidVersionInfo.getPendingVersion();

        if (isPendingStatus(pendingVersion, latestVersion, clientVersion)) {
            return AppVersionStatus.PENDING;
        }

        if (isUpdateStatus(minVersion, clientVersion)) {
            return AppVersionStatus.UPDATE;
        }

        return AppVersionStatus.OK;
    }

    public AppVersionStatus findIosVersionStatus(String clientVersion) {

        AppVersion iosVersionInfo = appVersionRepository.findIosVersionInfo();
        String minVersion = iosVersionInfo.getMinVersion();
        String latestVersion = iosVersionInfo.getLatestVersion();
        String pendingVersion = iosVersionInfo.getPendingVersion();

        if (isPendingStatus(pendingVersion, latestVersion, clientVersion)) {
            return AppVersionStatus.PENDING;
        }

        if (isUpdateStatus(minVersion, clientVersion)) {
            return AppVersionStatus.UPDATE;
        }

        return AppVersionStatus.OK;
    }

    private boolean isPendingStatus(String pendingVersion, String latestVersion, String clientVersion) {
        return (compareVersionParts(pendingVersion, latestVersion) == 0) && (compareVersionParts(latestVersion, clientVersion) == 1);
    }

    private boolean isUpdateStatus(String minVersion, String clientVersion) {
        return compareVersionParts(minVersion, clientVersion) == -1;
    }

    //system version이 client version보다 높으면 -1, 낮으면 1, 같으면 0 반환
    private int compareVersionParts(String systemVersion, String clientVersion) {
        int[] systemVersionParts = parseVersionParts(systemVersion);
        int[] clientVersionParts = parseVersionParts(clientVersion);

        for (int i = 0; i < systemVersionParts.length; i++) {
            if (clientVersionParts[i] < systemVersionParts[i]) {
                return -1;
            } else if (clientVersionParts[i] > systemVersionParts[i]) {
                return 1;
            }
        }
        return 0;
    }

    private int[] parseVersionParts(String version) {
        return Arrays.stream(version.split("\\.")).mapToInt(Integer::parseInt).toArray();
    }
}
