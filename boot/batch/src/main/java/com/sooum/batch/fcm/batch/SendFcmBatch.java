package com.sooum.batch.fcm.batch;

import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.sooum.batch.fcm.service.FcmSchedulerService;
import com.sooum.data.notification.entity.FcmSchedulerContent;
import com.sooum.data.notification.repository.FcmSchedulerContentRepository;
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

import java.util.HashMap;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SendFcmBatch {
    private static final int CHUNK_SIZE = 100;
    private final PlatformTransactionManager transactionManager;
    private final JobLauncher jobLauncher;
    private final JobRepository jobRepository;
    private final EntityManagerFactory entityManagerFactory;
    private final FcmSchedulerContentRepository fcmSchedulerContentRepository;
    private final FcmSchedulerService fcmSchedulerService;


    @Scheduled(cron = "0 0 21 * * *")
    public void runJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(fcmSendJob(), jobParameters);
    }

    @Bean
    public Job fcmSendJob() {
        return new JobBuilder("FCMSendJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(fcmSendStep())
                .build();
    }

    @Bean
    public Step fcmSendStep() {
        return new StepBuilder("FcmSendStep", jobRepository)
                .<String, String>chunk(CHUNK_SIZE, transactionManager)
                .reader(fcmReader())
                .writer(fcmWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<String> fcmReader() {
        JpaPagingItemReader<String> reader = new JpaPagingItemReader<>();
        reader.setName("FCMTokenReader");
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("select m.firebaseToken from Member m where m.isAllowNotify = true and m.firebaseToken is not null order by m.pk");
        reader.setPageSize(CHUNK_SIZE);
        return reader;
    }

    @Bean
    public ItemWriter<String> fcmWriter() {
        FcmSchedulerContent fcmSchedulerContent = fcmSchedulerContentRepository.findByPk(1L).orElseThrow(RuntimeException::new);
        return chunk -> {
            MulticastMessage message = MulticastMessage.builder()
                    .setNotification(
                            Notification.builder()
                                    .setTitle(fcmSchedulerContent.getTitle())
                                    .setBody(fcmSchedulerContent.getContent())
                                    .build()
                    )
                    .putAllData(generateSystemFcmData())
                    .addAllTokens((List<String>) chunk.getItems())
                    .build();
            fcmSchedulerService.send(message);
        };
    }

    private static HashMap<String, String> generateSystemFcmData() {
        HashMap<String, String> data = new HashMap<>();
        data.put("notificationType", "feed");
        return data;
    }
}
