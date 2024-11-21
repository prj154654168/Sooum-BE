package com.sooum.batch.img.batch;

import com.sooum.batch.img.service.ImgService;
import com.sooum.data.img.entity.Img;
import com.sooum.data.img.entity.ProfileImg;
import com.sooum.data.img.service.ProfileImgService;
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
public class DeleteProfileImgBatch {
    private static final int CHUNK_SIZE = 1000;
    private final PlatformTransactionManager transactionManager;
    private final JobRepository jobRepository;
    private final JobLauncher jobLauncher;
    private final EntityManagerFactory entityManagerFactory;
    private final ImgService imgService;
    private final ProfileImgService profileImgService;

    @Scheduled(cron = "0 30 3 * * *")
    public void runJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(deleteProfileImgJob(), jobParameters);
    }

    @Bean
    public Job deleteProfileImgJob() {
        return new JobBuilder("DeleteProfileImgBatchJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(deleteProfileImgStep())
                .build();
    }

    @Bean
    public Step deleteProfileImgStep() {
        return new StepBuilder("DeleteProfileImgStep", jobRepository)
                .<ProfileImg, ProfileImg>chunk(CHUNK_SIZE, transactionManager)
                .reader(unusedProfileImgReader())
                .writer(unUsedProfileImgDelete())
                .build();
    }

    @Bean
    public JpaPagingItemReader<ProfileImg> unusedProfileImgReader() {
        JpaPagingItemReader<ProfileImg> reader = new JpaPagingItemReader<>() {
            @Override
            public int getPage() {
                return 0;
            }
        };
        reader.setName("UnusedProfileImgReader");
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("select pi from ProfileImg pi where pi.profileOwner is null");
        reader.setPageSize(CHUNK_SIZE);
        return reader;
    }

    @Bean
    public ItemWriter<ProfileImg> unUsedProfileImgDelete() {
        return chuck -> {
            List<ProfileImg> profileImgs = (List<ProfileImg>) chuck.getItems();
            List<String> imgsName = profileImgs.stream().map(Img::getImgName).toList();
            imgService.deleteProfileImgs(imgsName);
            profileImgService.deleteProfileImgs(profileImgs);
        };
    }
}

