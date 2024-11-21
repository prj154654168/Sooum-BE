package com.sooum.batch.img.batch;

import com.sooum.batch.img.service.ImgService;
import com.sooum.data.img.entity.CardImg;
import com.sooum.data.img.entity.Img;
import com.sooum.data.img.service.CardImgService;
import jakarta.persistence.EntityManagerFactory;
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
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DeleteCardImgBatch {
    private static final int CHUNK_SIZE = 1000;
    private final PlatformTransactionManager transactionManager;
    private final JobLauncher jobLauncher;
    private final JobRepository jobRepository;
    private final EntityManagerFactory entityManagerFactory;
    private final CardImgService cardImgService;
    private final ImgService imgService;

    @Scheduled(cron = "0 0 3 * * *")
    public void runJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(deleteCardImgJob(), jobParameters);
    }

    @Bean
    public Job deleteCardImgJob() {
        return new JobBuilder("DeleteCardImgBatchJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(deleteCardImgStep())
                .build();
    }

    @Bean
    public Step deleteCardImgStep() {
        return new StepBuilder("DeleteCardImgStep", jobRepository)
                .<CardImg, CardImg>chunk(CHUNK_SIZE, transactionManager)
                .reader(unusedCardImgReader())
                .writer(unusedCardImgDelete())
                .build();
    }
    @Bean
    public JpaPagingItemReader<CardImg> unusedCardImgReader() {
        JpaPagingItemReader<CardImg> reader = new JpaPagingItemReader<>() {
            @Override
            public int getPage() {
                return 0;
            }
        };
        reader.setName("UnusedCardImgReader");
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("select ci from CardImg ci where ci.feedCard is null and ci.commentCard is null");
        reader.setPageSize(CHUNK_SIZE);
        return reader;
    }

    @Bean
    public ItemWriter<CardImg> unusedCardImgDelete() {
        return chuck -> {
            List<CardImg> cardImgs = (List<CardImg>) chuck.getItems();
            List<String> imgsName = cardImgs.stream().map(Img::getImgName).toList();
            imgService.deleteCardImgs(imgsName);
            cardImgService.deleteCardImgs(cardImgs);
        };
    }
}

