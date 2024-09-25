package com.sooum.core.domain.rsa.batch;

import com.sooum.core.domain.rsa.service.RsaService;
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
public class RsaKeyGenerateConfig {

    private final String JOB_NAME = "rsaKeyGenerateJob";
    private final String STEP_NAME = "rsaKeyGenerateStep";

    private final RsaService rsaService;
    private final JobLauncher jobLauncher;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job rsaKeyGenerateJob() {
        return new JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(rsaKeyGenerateStep())
                .build();
    }

    @Bean
    @JobScope
    public Step rsaKeyGenerateStep() {
        return new StepBuilder(STEP_NAME, jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("rsaKeyGenerateStep start");
                    rsaService.save();
                    log.info("rsaKeyGenerateStep end");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Scheduled(cron = "0 0 0 1 */6 *")  // 6개월마다 1일 00:00에 실행
    public void runRsaKeyGenerateJob() throws Exception {
        log.info("RSA 키 생성 배치 작업 실행");
        jobLauncher.run(rsaKeyGenerateJob(), new JobParameters());
    }
}
