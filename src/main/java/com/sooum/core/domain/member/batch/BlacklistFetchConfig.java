package com.sooum.core.domain.member.batch;

import com.sooum.core.domain.member.entity.Blacklist;
import com.sooum.core.domain.member.repository.BlacklistRepository;
import jakarta.persistence.EntityManagerFactory;
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
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.redis.RedisItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BlacklistFetchConfig {

    private final String JOB_NAME = "blacklistFetchJob";
    private final String FIRST_STEP_NAME = "blacklistFetchStep";
    private final String SECOND_STEP_NAME = "blacklistCacheStep";
    private final int CHUNK_SIZE = 100;

    private final BlacklistRepository blacklistRepository;
    private final JobLauncher jobLauncher;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final RedisTemplate<String, Object> redisTemplate;

    @Bean
    public Job blacklistFetchJob() {
        return new JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(blacklistFetchStep())
                .next(blacklistCacheStep())
                .build();
    }

    @Bean
    @JobScope
    public Step blacklistFetchStep() {
        return new StepBuilder(FIRST_STEP_NAME, jobRepository)
                .<Blacklist, Blacklist>chunk(CHUNK_SIZE, transactionManager)
                .reader(blacklistReader())
                .processor(blacklistFetchProcessor())
                .writer(blacklistFetchWriter())
                .build();
    }

    @Bean
    @JobScope
    public Step blacklistCacheStep() {
        return new StepBuilder(SECOND_STEP_NAME, jobRepository)
                .<Blacklist, Void>chunk(CHUNK_SIZE, transactionManager)
                .reader(blacklistReader())
                .writer(blacklistCacheWriter())
                .build();

    }

    @Bean
    public JpaPagingItemReader<Blacklist> blacklistReader() {
        return new JpaPagingItemReaderBuilder<Blacklist>()
                .name(JOB_NAME + "Reader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
                .queryString("SELECT b FROM Blacklist b")
                .build();
    }

    @Bean
    public ItemProcessor<Blacklist, Blacklist> blacklistFetchProcessor() {
        return blacklist -> blacklist.getExpiredAt().isBefore(LocalDateTime.now()) ? blacklist : null;
    }

    @Bean
    public ItemWriter<Blacklist> blacklistFetchWriter() {
        return blacklists -> {
            if (!blacklists.isEmpty()) {
                List<String> tokens = blacklists.getItems().stream()
                        .map(Blacklist::getToken)
                        .toList();

                blacklistRepository.deleteByTokenIn(tokens);
            }
        };
    }

    @Bean
    public RedisItemWriter<String, Object> blacklistCacheWriter() {
        return null;    // 추후 생성
    }

    private long calculateTTLForBlacklist(Blacklist blacklist) {
        return blacklist.getExpiredAt().getSecond() - LocalDateTime.now().getSecond();
    }



    @Scheduled(cron = "0 0 0 * * *")
    public void runBlacklistBackupStep() throws Exception {
        jobLauncher.run(blacklistFetchJob(), new JobParameters());
    }
}
