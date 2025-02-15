package com.sooum.batch.rsa;

import com.sooum.batch.rsa.service.RsaSchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class RsaScheduler {
    private final RsaSchedulerService rsaSchedulerService;
    @Scheduled(cron = "0 0 6 * * ?")
    public void schedule() {
        try {
            log.info("========RsaScheduler start========");
            rsaSchedulerService.save(LocalDateTime.now());
        } catch (NoSuchAlgorithmException e) {
            log.info(e.getMessage());
        }
    }
}
