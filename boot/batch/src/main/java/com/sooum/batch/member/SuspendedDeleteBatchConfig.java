package com.sooum.batch.member;

import com.sooum.batch.member.service.DeleteSuspensionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.repeat.RepeatStatus;
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

    private final DeleteSuspensionService deleteSuspensionService;

    @Scheduled(cron = "0 30 4 * * ?")
    public void runJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(deleteSuspendedDataJob(), jobParameters);
    }

    @Bean
    public Job deleteSuspendedDataJob() throws Exception {
        log.info("deleteSuspendedDataJob()");
        return new JobBuilder("deleteSuspendedDataJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(deleteExpiredSuspendedTasklet())
                .build();
    }

    @Bean
    public TaskletStep deleteExpiredSuspendedTasklet() {
        log.info("deleteExpiredSuspendedTasklet()");
        return new StepBuilder("deleteExpiredSuspendedTasklet", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    deleteSuspensionService.deleteExpiredSuspended();
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}