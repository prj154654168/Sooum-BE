package com.sooum.api;

import com.sooum.api.img.service.AWSImgService;
import com.sooum.client.aws.rekognition.RekognitionService;
import com.sooum.client.aws.s3.S3FCMService;
import com.sooum.client.aws.s3.S3ImgService;
import com.sooum.client.aws.s3.imgproperties.S3ImgPathProperties;
import com.sooum.config.aws.rekognition.RekognitionConfig;
import com.sooum.config.aws.s3.S3Config;
import com.sooum.global.config.fcm.FcmConfig;
import com.sooum.global.config.jwt.JwtProperties;
import com.sooum.global.config.redis.RedisConfig;
import com.sooum.global.config.security.path.ExcludeAuthPathProperties;
import com.sooum.global.config.warmer.ApplicationWarmer;
import com.sooum.global.slack.service.SlackService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.BDDMockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public abstract class IntegrationTestSupport {

    @MockBean
    protected S3Config s3Config;
    @MockBean
    protected FcmConfig fcmConfig;
    @MockBean
    protected RekognitionConfig rekognitionConfig;

    @MockBean
    protected S3ImgPathProperties s3ImgPathProperties;
    @MockBean
    protected JwtProperties jwtProperties;

    @MockBean
    protected AWSImgService awsImgService;
    @MockBean
    protected SlackService slackService;
    @MockBean
    protected S3FCMService s3FCMService;
    @MockBean
    protected RekognitionService rekognitionService;
    @MockBean
    protected S3ImgService s3ImgService;

    @MockBean
    protected ApplicationWarmer applicationWarmer;

    @MockBean
    protected RedisConfig redisConfig;
    @MockBean
    protected RedisConnectionFactory redisConnectionFactory;
    @MockBean
    protected RedisTemplate<String, LocalDateTime> stringLocalDateTimeRedisTemplate;
    @MockBean
    protected CacheManager cacheManager;

    @MockBean
    ExcludeAuthPathProperties excludeAuthPathProperties;
    @BeforeEach
    void setUp() {
        BDDMockito.given(excludeAuthPathProperties.getExcludeAuthPaths()).willReturn(List.of());
    }

}
