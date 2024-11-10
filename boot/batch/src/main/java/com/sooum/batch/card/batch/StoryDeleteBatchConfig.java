package com.sooum.batch.card.batch;

import com.sooum.batch.card.batch.dto.CommentRelatedEntitiesDeletionDto;
import com.sooum.batch.card.batch.dto.FeedRelatedEntitiesDeletionDto;
import com.sooum.batch.card.batch.repository.*;
import com.sooum.data.card.entity.CommentCard;
import com.sooum.data.card.entity.CommentLike;
import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.card.entity.FeedLike;
import com.sooum.data.report.entity.CommentReport;
import com.sooum.data.report.entity.FeedReport;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class StoryDeleteBatchConfig {

    private final JobLauncher jobLauncher;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final JobRepository jobRepository;
    private static final int CHUNK_SIZE = 100;

    private final CommentCardBatchRepository commentCardBatchRepository;
    private final CommentLikeBatchRepository commentLikeBatchRepository;
    private final CommentReportBatchRepository commentReportBatchRepository;
    private final FeedCardBatchRepository feedCardBatchRepository;
    private final FeedLikeBatchRepository feedLikeBatchRepository;
    private final FeedReportBatchRepository feedReportBatchRepository;

    @Scheduled(cron = "0 0 4 * * ?")
//    @Scheduled(cron = "0 */1 * * * ?")
    public void runJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(commentCardReaderJob(), jobParameters);
    }

    @Bean
    public Job commentCardReaderJob() {
        log.info("commentCardReaderJob");
        return new JobBuilder("commentCardReaderJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(deletedStoryCommentCardStep())
                    .on(ExitStatus.COMPLETED.getExitCode())
                    .to(deletedStoryFeedCardStep())
                .from(deletedStoryFeedCardStep())
                    .end()
                .build();
    }

    @Bean
    public Step deletedStoryCommentCardStep() {
        log.info("deletedStoryCommentCardStep");
        return new StepBuilder("deletedStoryCommentCardStep", jobRepository)
                .<FeedCard, CommentRelatedEntitiesDeletionDto>chunk(CHUNK_SIZE, transactionManager)
                .reader(deletedStoryFeedReaderInCommentCardStep())
                .processor(commentRelatedEntitiesDeletionProcessor())
                .writer(commentRelatedEntitiesDeletionWriter())
                .build();
    }

    @Bean
    public Step deletedStoryFeedCardStep() {
        log.info("deletedStoryFeedCardStep");
        return new StepBuilder("deletedStoryFeedCardStep", jobRepository)
                .<FeedCard, FeedRelatedEntitiesDeletionDto>chunk(CHUNK_SIZE, transactionManager)
                .reader(deletedStoryFeedReaderInFeedCardStep())
                .processor(feedRelatedEntitiesDeletionProcessor())
                .writer(feedDeletionWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<FeedCard> deletedStoryFeedReaderInCommentCardStep() {
        return new JpaPagingItemReaderBuilder<FeedCard>()
                .name("deletedStoryFeed")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select fc from FeedCard fc where fc.isStory = true and fc.createdAt < (current_timestamp - 1 day) order by fc.pk")
                .pageSize(CHUNK_SIZE).build();
    }

    @Bean
    public JpaPagingItemReader<FeedCard> deletedStoryFeedReaderInFeedCardStep() {

        JpaPagingItemReader<FeedCard> feedCardJpaPagingItemReader = new JpaPagingItemReader<>(){
            @Override
            public int getPage() {
                return 0;
            }
        };
        feedCardJpaPagingItemReader.setName("deletedStoryFeed");
        feedCardJpaPagingItemReader.setEntityManagerFactory(entityManagerFactory);
        feedCardJpaPagingItemReader.setQueryString("select fc from FeedCard fc where fc.isStory = true and fc.createdAt < (current_timestamp - 1 day) order by fc.pk");
        feedCardJpaPagingItemReader.setPageSize(CHUNK_SIZE);

        return feedCardJpaPagingItemReader;
    }

    @Bean
    public ItemProcessor<FeedCard, CommentRelatedEntitiesDeletionDto> commentRelatedEntitiesDeletionProcessor() {
        return commentCard -> {
            List<CommentCard> commentCardsForDeletion = commentCardBatchRepository.findCommentCardsForDeletion(commentCard.getPk());
            List<CommentLike> commentLikesForDeletion = commentLikeBatchRepository.findCommentLikesForDeletion(commentCardsForDeletion);
            List<CommentReport> commentReportsForDeletion = commentReportBatchRepository.findCommentReportsForDeletion(commentCardsForDeletion);
            return CommentRelatedEntitiesDeletionDto.builder()
                    .commentCards(commentCardsForDeletion)
                    .commentLike(commentLikesForDeletion)
                    .commentReports(commentReportsForDeletion)
                    .build();
        };
    }

    @Bean
    public ItemProcessor<FeedCard, FeedRelatedEntitiesDeletionDto> feedRelatedEntitiesDeletionProcessor() {
        return feedCard -> {
            List<FeedLike> feedLikesForDeletion = feedLikeBatchRepository.findFeedLikesForDeletion(feedCard);
            List<FeedReport> feedReportsForDeletion = feedReportBatchRepository.findFeedReportsForDeletion(feedCard);
            return FeedRelatedEntitiesDeletionDto.builder()
                    .feedCard(feedCard)
                    .feedLikes(feedLikesForDeletion)
                    .feedReports(feedReportsForDeletion)
                    .build();
        };
    }

    @Bean
    public ItemWriter<CommentRelatedEntitiesDeletionDto> commentRelatedEntitiesDeletionWriter() {
        return items -> {
            for (CommentRelatedEntitiesDeletionDto deletionDto : items) {
                commentReportBatchRepository.deleteAllInBatch(deletionDto.getCommentReports());
                commentLikeBatchRepository.deleteAllInBatch(deletionDto.getCommentLikes());
                commentCardBatchRepository.deleteAllInBatch(deletionDto.getCommentCards());
            }
        };
    }

    @Bean
    public ItemWriter<FeedRelatedEntitiesDeletionDto> feedDeletionWriter() {
        return items -> {
            for (FeedRelatedEntitiesDeletionDto deletionDto : items) {
                feedReportBatchRepository.deleteAllInBatch(deletionDto.getFeedReports());
                feedLikeBatchRepository.deleteAllInBatch(deletionDto.getFeedLikes());
                feedCardBatchRepository.delete(deletionDto.getFeedCard());
            }
        };
    }
}
