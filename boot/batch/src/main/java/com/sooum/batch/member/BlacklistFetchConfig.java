package com.sooum.batch.member;

import com.sooum.data.member.entity.Blacklist;
import com.sooum.data.member.repository.BlacklistRepository;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
                .<Blacklist, Blacklist>chunk(CHUNK_SIZE, transactionManager)
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
    public ItemWriter<Blacklist> blacklistCacheWriter() {
        return blacklists -> {
            redisTemplate.executePipelined((RedisCallback<?>) (redisConnection) -> {
                for (Blacklist blacklist : blacklists) {
                    String key = "blacklist:" + blacklist.getToken();
                    long ttl = calculateTTLForBlacklist(blacklist);
                    redisTemplate.opsForValue().setIfAbsent(key, blacklist, ttl, TimeUnit.SECONDS);
                }
                return null;
            });
        };
    }

    private long calculateTTLForBlacklist(Blacklist blacklist) {
        return Duration.between(LocalDateTime.now(), blacklist.getExpiredAt()).getSeconds();
    }



    @Scheduled(cron = "0 0 0 * * *")
    public void runBlacklistBackupStep() throws Exception {
        jobLauncher.run(blacklistFetchJob(), new JobParameters());
    }
}
