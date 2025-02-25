package com.sooum.api.appversion.controller;

import com.sooum.api.appversion.dto.AppVersionStatus;
import com.sooum.api.appversion.service.AppVersionService;
import com.sooum.data.app.repository.AppVersionFlagRepository;
import com.sooum.data.app.repository.AppVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/version")
@RequiredArgsConstructor
public class AppVersionController {
    private final AppVersionRepository appVersionRepository;
    private final AppVersionFlagRepository appVersionFlagRepository;
    private final AppVersionService appVersionService;

    @GetMapping("/android")
    public ResponseEntity<String> android() {
        return ResponseEntity.ok(appVersionRepository.findAndroidLatestVersion());
    }

    @GetMapping("/ios")
    public ResponseEntity<String> ios() {
        return ResponseEntity.ok(appVersionRepository.findIosLatestVersion());
    }

    @GetMapping("/flag")
    public ResponseEntity<Boolean> flag() {
        return ResponseEntity.ok(appVersionFlagRepository.findFlag());
    }

    @GetMapping("/ios/v2")
    public ResponseEntity<AppVersionStatus> iosV2(@RequestParam String version) {
        return ResponseEntity.ok(appVersionService.findIosVersionStatus(version));
    }

    @GetMapping("/android/v2")
    public ResponseEntity<AppVersionStatus> androidV2(@RequestParam String version) {
        return ResponseEntity.ok(appVersionService.findAndroidVersionStatus(version));
    }
}
