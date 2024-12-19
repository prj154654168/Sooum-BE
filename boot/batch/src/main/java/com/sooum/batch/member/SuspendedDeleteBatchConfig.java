package com.sooum.batch.member;

import com.sooum.data.suspended.entity.Suspended;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class SuspendedDeleteBatchConfig {
    private final JobLauncher jobLauncher;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Scheduled(cron = "0 0 0 * * 1") // 매주 월요일 자정 실행
    public void runJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(deleteSuspendedDataJob(), jobParameters);
    }

    @Bean
    public Job deleteSuspendedDataJob() throws Exception {
        log.info("deleteSuspendedDataJob()");
        new JobBuilder("deleteSuspendedDataJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(deleteSuspendedDataStep())
                .build();

    }

    @Bean
    public Step deleteSuspendedDataStep() {
        log.info("deleteSuspendedDataStep()");
        return new StepBuilder("deleteSuspendedDataStep", jobRepository)
                .<Suspended, Suspended>chunk()
    }

}
