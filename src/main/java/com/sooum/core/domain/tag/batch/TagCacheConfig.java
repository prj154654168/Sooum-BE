package com.sooum.core.domain.tag.batch;

import com.sooum.core.domain.tag.entity.CachedTag;
import com.sooum.core.domain.tag.entity.Tag;
import com.sooum.core.domain.tag.service.TagService;
import lombok.RequiredArgsConstructor;
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
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class TagCacheConfig {

    private final String JOB_NAME = "tagCacheJob";
    private final String STEP_NAME = "tagCacheStep";
    private final int CHUNK_SIZE = 100;

    private final JobLauncher jobLauncher;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final TagService tagService;
    private final DataSource dataSource;

    @Bean
    public Job tagCacheJob() {
        return new JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(tagCacheStep())
                .build();
    }

    @Bean
    @JobScope
    public Step tagCacheStep() {
        return new StepBuilder(STEP_NAME, jobRepository)
                .<Tag, CachedTag>chunk(CHUNK_SIZE, transactionManager)
                .reader(tagReader())
                .processor(tagProcessor())
                .writer(tagWriter())
                .build();
    }

    @Bean
    public JdbcPagingItemReader<Tag> tagReader() {
        JdbcPagingItemReader<Tag> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(dataSource);
        reader.setFetchSize(CHUNK_SIZE);
        reader.setRowMapper(new BeanPropertyRowMapper<>(Tag.class));
        try {
            reader.setQueryProvider(pagingQueryProvider().getObject());
        } catch (Exception e) {
            throw new RuntimeException("Paging query provider 설정 오류", e);
        }
        return reader;
    }

    @Bean
    public SqlPagingQueryProviderFactoryBean pagingQueryProvider() {
        SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();
        provider.setDataSource(dataSource);
        provider.setSelectClause("SELECT CONTENT, COUNT");
        provider.setFromClause("FROM tag");
        provider.setWhereClause("WHERE IS_ACTIVE = true");
        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("count", Order.DESCENDING);
        provider.setSortKeys(sortKeys);
        return provider;
    }

    @Bean
    public ItemProcessor<Tag, CachedTag> tagProcessor() {
        return tag -> new CachedTag(tag.getContent(), tag.getCount());
    }


    @Bean
    public ItemWriter<CachedTag> tagWriter() {
        return chunk -> tagService.upsert(chunk.getItems());
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void runTagCacheStep() throws Exception {
        jobLauncher.run(tagCacheJob(), new JobParameters());
    }
}

