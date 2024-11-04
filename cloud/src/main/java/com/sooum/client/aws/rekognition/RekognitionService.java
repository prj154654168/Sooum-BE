package com.sooum.client.aws.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.DetectModerationLabelsRequest;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.ModerationLabel;
import software.amazon.awssdk.services.rekognition.model.S3Object;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RekognitionService {
    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    private final RekognitionClient rekognitionClient;

    public List<ModerationLabel> getModerationLabels(DetectModerationLabelsRequest build) {
        return rekognitionClient.detectModerationLabels(build).moderationLabels();
    }

    public boolean isModeratingImg(String filePath, String imgName) {
        DetectModerationLabelsRequest build = DetectModerationLabelsRequest.builder()
                .minConfidence(70F)
                .image(Image.builder()
                        .s3Object(S3Object.builder()
                                .bucket(bucket)
                                .name(filePath + imgName)
                                .build())
                        .build())
                .build();

        List<ModerationLabel> moderationLabels = getModerationLabels(build);
        for (ModerationLabel label : moderationLabels) {
            if (isExplicitNudity(label) || isExplicitSexualActivity(label) || isSexToys(label)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isSexToys(ModerationLabel label) {
        return label.name().equalsIgnoreCase("Sex Toys") && label.taxonomyLevel().equals(2);
    }

    private static boolean isExplicitSexualActivity(ModerationLabel label) {
        return label.name().equalsIgnoreCase("Explicit Sexual Activity") && label.taxonomyLevel().equals(2);
    }

    private static boolean isExplicitNudity(ModerationLabel label) {
        return label.name().equalsIgnoreCase("Explicit Nudity") && label.taxonomyLevel().equals(2);
    }
}
