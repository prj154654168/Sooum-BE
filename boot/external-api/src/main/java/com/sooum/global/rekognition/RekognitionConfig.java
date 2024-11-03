package com.sooum.global.rekognition;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;

@Configuration
public class RekognitionConfig {

    @Value("${spring.cloud.aws.rekognition.credentials.accessKey}")
    private String accessKey;
    @Value("${spring.cloud.aws.rekognition.credentials.secretKey}")
    private String secretKey;
    @Value("${spring.cloud.aws.region}")
    private String region;

    @Bean
    public RekognitionClient amazonRekognition() {
        return RekognitionClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey,secretKey)))
                .build();
    }
}
