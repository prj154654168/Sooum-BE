package com.sooum.batch.report.batch;

import com.sooum.data.report.service.CommentReportService;
import com.sooum.data.report.service.FeedReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ReportDeleteBatchConfig {
    private final JobLauncher jobLauncher;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final FeedReportService feedReportService;
    private final CommentReportService commentReportService;

    @Scheduled(cron = "0 30 4 * * * ")
    public void runBatch() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(deleteReportAfter6month(), jobParameters);
    }


    @Bean
    public Job deleteReportAfter6month() {
        return new JobBuilder("deleteReportAfter6monthJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(deleteFeedReportTasklet())
                .next(deleteCommentReportTasklet())
                .build();
    }

    @Bean
    public Step deleteFeedReportTasklet() {
        return new StepBuilder("deleteReportTasklet", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    feedReportService.deleteAllBefore6Month();
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step deleteCommentReportTasklet() {
        return new StepBuilder("deleteCommentReportTasklet", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    commentReportService.deleteAllBefore6Month();
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
