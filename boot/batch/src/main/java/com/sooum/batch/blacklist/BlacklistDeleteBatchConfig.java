package com.sooum.batch.blacklist;

import com.sooum.batch.blacklist.repository.BlacklistBatchRepository;
import com.sooum.data.member.entity.Blacklist;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class BlacklistDeleteBatchConfig {
    private final JobLauncher jobLauncher;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final int CHUNK_SIZE = 100;

    private final BlacklistBatchRepository blacklistBatchRepository;

    @Scheduled(cron = "0 45 4 * * ?")
    public void runJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(deleteBlacklistJob(), jobParameters);
    }

    @Bean
    public Job deleteBlacklistJob() {
        log.info("deleteBlacklistJob");
        return new JobBuilder("deleteBlacklistJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(deleteBlacklistStep())
                .build();
    }

    @Bean
    public Step deleteBlacklistStep() {
        log.info("deleteBlacklistStep");
        return new StepBuilder("deleteBlacklistStep", jobRepository)
                .<Blacklist, Blacklist>chunk(CHUNK_SIZE, transactionManager)
                .reader(deleteBlacklistReader())
                .writer(deleteBlacklistWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Blacklist> deleteBlacklistReader() {
        return new JpaPagingItemReaderBuilder<Blacklist>()
                .name("deleteBlacklistReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select b from Blacklist b where b.expiredAt < current_timestamp")
                .pageSize(CHUNK_SIZE)
                .build();
    }

    @Bean
    public ItemWriter<Blacklist> deleteBlacklistWriter() {
        return items -> {
            List<String> tokens = items.getItems().stream()
                    .map(Blacklist::getToken)
                    .toList();
            blacklistBatchRepository.deleteAllByTokenIn(tokens);
        };
    }
}