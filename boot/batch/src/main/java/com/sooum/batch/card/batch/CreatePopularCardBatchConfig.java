package com.sooum.batch.card.batch;

import com.sooum.batch.card.batch.service.DeletePreviousPopularCard;
import com.sooum.batch.card.batch.service.SavePopularCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CreatePopularCardBatchConfig {
    private final JobLauncher jobLauncher;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final SavePopularCardService savePopularCardService;
    private final DeletePreviousPopularCard deletePreviousPopularCard;
    private static final String COMPLETED = "COMPLETED";
    private static final String FAILED = "FAILED";

    @Scheduled(cron = "0 */10 * * * *")
    public void runBatch() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(createPopularCardJob(), jobParameters);
    }

    @Bean
    public Job createPopularCardJob() {
        return new JobBuilder("createPopularCardJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(savePopularCardByLikeTasklet())
                    .on(COMPLETED)
                    .to(deletePreviousPopularCardByLikeTasklet())

                .from(savePopularCardByLikeTasklet())
                .on(FAILED)
                .to(savePopularCardByCommentTasklet())
                .on(COMPLETED)
                .to(deletePreviousPopularCardByCommentTasklet())

                .from(deletePreviousPopularCardByLikeTasklet())
                    .on("*")
                    .to(savePopularCardByCommentTasklet())
                    .on(COMPLETED)
                    .to(deletePreviousPopularCardByCommentTasklet())
                .end()
                .build();
    }

    @Bean
    public Step savePopularCardByLikeTasklet() {
        return new StepBuilder("savePopularCardByLikeTasklet", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    savePopularCardService.savePopularCardByLike();

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step deletePreviousPopularCardByLikeTasklet() {
        return new StepBuilder("deletePreviousPopularCardByLikeTasklet", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    deletePreviousPopularCard.deletePreviousPopularFeedsByLike();

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step savePopularCardByCommentTasklet() {
        return new StepBuilder("savePopularCardByCommentTasklet", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    savePopularCardService.savePopularCardByComment();

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public TaskletStep deletePreviousPopularCardByCommentTasklet() {
        return new StepBuilder("deletePreviousPopularCardByCommentTasklet", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    deletePreviousPopularCard.deletePreviousPopularFeedsByComment();

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
