package com.sooum.core.domain.member.batch;

import com.sooum.core.domain.member.service.BlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BlacklistBackupConfig {

    private final String JOB_NAME = "blacklistBackupJob";
    private final String STEP_NAME = "blacklistBackupStep";

    private final BlacklistService blacklistService;
    private final JobLauncher jobLauncher;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job blacklistBackupJob() {
        return new JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(blacklistBackupStep())
                .build();
    }

    @Bean
    @JobScope
    public Step blacklistBackupStep() {
        return new StepBuilder(STEP_NAME, jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("blacklistBackupStep start");
                    blacklistService.backup();
                    log.info("blacklistBackupStep end");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Scheduled(cron = "0 0 * * * *")
    public void runBlacklistBackupStep() throws Exception {
        log.info("블랙리스트 백업 배치 작업 실행");
        jobLauncher.run(blacklistBackupJob(), new JobParameters());
    }
}
