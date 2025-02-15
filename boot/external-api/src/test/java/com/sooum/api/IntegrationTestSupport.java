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
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

@SpringBootTest
public abstract class IntegrationTestSupport {

    @MockBean
    S3Config s3Config;
    @MockBean
    FcmConfig fcmConfig;
    @MockBean
    RekognitionConfig rekognitionConfig;

    @MockBean
    S3ImgPathProperties s3ImgPathProperties;
    @MockBean
    JwtProperties jwtProperties;

    @MockBean
    AWSImgService awsImgService;
    @MockBean
    SlackService slackService;
    @MockBean
    S3FCMService s3FCMService;
    @MockBean
    RekognitionService rekognitionService;
    @MockBean
    S3ImgService s3ImgService;

    @MockBean
    ApplicationWarmer applicationWarmer;

    @MockBean
    ExcludeAuthPathProperties excludeAuthPathProperties;
    @BeforeEach
    void setUp() {
        BDDMockito.given(excludeAuthPathProperties.getExcludeAuthPaths()).willReturn(List.of());
    }

}
