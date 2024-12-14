package com.sooum.api.appversion.controller;

import com.sooum.data.app.repository.AppVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/version")
@RequiredArgsConstructor
public class AppVersionController {
    private final AppVersionRepository appVersionRepository;

    @GetMapping("/android")
    public ResponseEntity<String> android() {
        return ResponseEntity.ok(appVersionRepository.findAndroidLatestVersion());
    }

    @GetMapping("/ios")
    public ResponseEntity<String> ios() {
        return ResponseEntity.ok(appVersionRepository.findIosLatestVersion());
    }
}
