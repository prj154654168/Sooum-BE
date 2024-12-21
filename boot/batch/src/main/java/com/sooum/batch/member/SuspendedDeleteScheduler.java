package com.sooum.batch.member;

import com.sooum.batch.member.service.DeleteSuspensionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class SuspendedDeleteScheduler {
    private final DeleteSuspensionService deleteSuspensionService;

    @Scheduled(cron = "0 30 4 * * ?")
    public void schedule() {
        log.info("====Suspended Scheduler started====");
        deleteSuspensionService.deleteExpiredSuspended();
    }
}