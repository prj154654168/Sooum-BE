package com.sooum.batch;

import com.sooum.client.aws.rekognition.RekognitionService;
import com.sooum.client.aws.s3.S3FCMService;
import com.sooum.client.aws.s3.S3ImgService;
import com.sooum.client.aws.s3.imgproperties.S3ImgPathProperties;
import com.sooum.config.aws.rekognition.RekognitionConfig;
import com.sooum.config.aws.s3.S3Config;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public abstract class IntegrationTestSupport {

    @MockBean
    S3Config s3Config;
    @MockBean
    RekognitionConfig rekognitionConfig;

    @MockBean
    S3ImgPathProperties s3ImgPathProperties;

    @MockBean
    S3FCMService s3FCMService;
    @MockBean
    RekognitionService rekognitionService;
    @MockBean
    S3ImgService s3ImgService;
}
