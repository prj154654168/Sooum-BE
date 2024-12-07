package com.sooum.global.rsa;

import com.sooum.global.rsa.service.RsaSchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;

@Slf4j
@Component
@RequiredArgsConstructor
public class RsaScheduler {
    private final RsaSchedulerService rsaSchedulerService;
    @Scheduled(cron = "0 0 6 * * ?")
    public void schedule() {
        try {
            log.info("========RsaScheduler start========");
            rsaSchedulerService.save();
        } catch (NoSuchAlgorithmException e) {
            log.info(e.getMessage());
        }
    }
}
