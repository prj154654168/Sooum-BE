package com.sooum.client.aws.s3.imgproperties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class S3ImgPathProperties {
    @Value("${spring.cloud.aws.s3.USER_CARD_IMG}")
    private String DEFAULT_CARD_IMG_PATH;
    @Value("${spring.cloud.aws.s3.USER_CARD_IMG}")
    private String USER_CARD_IMG_PATH;
    @Value("${spring.cloud.aws.s3.PROFILE_IMG}")
    private String PROFILE_IMG_PATH;

}
