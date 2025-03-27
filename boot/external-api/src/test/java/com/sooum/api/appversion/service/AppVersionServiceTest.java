package com.sooum.api.appversion.service;

import com.sooum.api.appversion.dto.AppVersionStatus;
import com.sooum.data.app.entity.AppVersion;
import com.sooum.data.app.repository.AppVersionRepository;
import com.sooum.data.member.entity.devicetype.DeviceType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@ExtendWith(MockitoExtension.class)
class AppVersionServiceTest {

    @Mock
    private AppVersionRepository appVersionRepository;
    @InjectMocks
    private AppVersionService appVersionService;


    @DisplayName("min,latest,pending column 최초 추가 시점의 앱 버전 조회 시나리오")
    @TestFactory
    Collection<DynamicTest> T_AppVersionAPIV2_최초_추가_시나리오() {
        //given
        AppVersion appVersion = AppVersion.builder()
                .deviceType(DeviceType.ANDROID)
                .latestVersion("1.0.0")
                .pendingVersion("1.0.0")
                .minVersion("1.0.0")
                .build();

        return List.of(
                dynamicTest("min version보다 낮은 버전에는 update를 반환한다", () -> {
                    //given
                    String clientVersion = "0.0.9";

                    //when
                    BDDMockito.given(appVersionRepository.findAndroidVersionInfo()).willReturn(appVersion);

                    //then
                    Assertions.assertThat(appVersionService.findAndroidVersionStatus(clientVersion)).isEqualTo(AppVersionStatus.UPDATE);
                }),
                dynamicTest("latest version과 동일한 버전에는 ok를 반환한다.", () -> {
                    //given
                    String clientVersion = "1.0.0";

                    //when
                    BDDMockito.given(appVersionRepository.findAndroidVersionInfo()).willReturn(appVersion);

                    //then
                    Assertions.assertThat(appVersionService.findAndroidVersionStatus(clientVersion)).isEqualTo(AppVersionStatus.OK);
                })
        );

    }

    @DisplayName("APP 심사 시점의 앱 버전 조회 시나리오")
    @TestFactory
    Collection<DynamicTest> T_AppVersionAPIV2_심사_시나리오() {
        //given
        AppVersion appVersion = AppVersion.builder()
                .deviceType(DeviceType.ANDROID)
                .latestVersion("1.0.0")
                .pendingVersion("1.0.1")
                .minVersion("1.0.0")
                .build();

        return List.of(
                dynamicTest("min version보다 낮은 버전에는 update를 반환한다", () -> {
                    //given
                    String clientVersion = "0.0.9";

                    //when
                    BDDMockito.given(appVersionRepository.findAndroidVersionInfo()).willReturn(appVersion);

                    //then
                    Assertions.assertThat(appVersionService.findAndroidVersionStatus(clientVersion)).isEqualTo(AppVersionStatus.UPDATE);
                }),
                dynamicTest("latest version과 동일한 버전에는 ok를 반환한다.", () -> {
                    //given
                    String clientVersion = "1.0.0";

                    //when
                    BDDMockito.given(appVersionRepository.findAndroidVersionInfo()).willReturn(appVersion);

                    //then
                    Assertions.assertThat(appVersionService.findAndroidVersionStatus(clientVersion)).isEqualTo(AppVersionStatus.OK);
                }),
                dynamicTest("pending version과 동일한 버전에는 pending을 반환한다.", () -> {
                    //given
                    String clientVersion = "1.0.1";

                    //when
                    BDDMockito.given(appVersionRepository.findAndroidVersionInfo()).willReturn(appVersion);

                    //then
                    Assertions.assertThat(appVersionService.findAndroidVersionStatus(clientVersion)).isEqualTo(AppVersionStatus.PENDING);
                })
        );

    }

    @DisplayName("APP 심사 통과 후 앱 버전 조회 시나리오")
    @TestFactory
    Collection<DynamicTest> T_AppVersionAPIV2_새로운_버전_배포_후_시나리오() {
        //given
        AppVersion appVersion = AppVersion.builder()
                .deviceType(DeviceType.ANDROID)
                .latestVersion("1.0.1")
                .pendingVersion("1.0.1")
                .minVersion("1.0.0")
                .build();

        return List.of(
                dynamicTest("min version보다 낮은 버전에는 update를 반환한다", () -> {
                    //given
                    String clientVersion = "0.0.9";

                    //when
                    BDDMockito.given(appVersionRepository.findAndroidVersionInfo()).willReturn(appVersion);

                    //then
                    Assertions.assertThat(appVersionService.findAndroidVersionStatus(clientVersion)).isEqualTo(AppVersionStatus.UPDATE);
                }),
                dynamicTest("latest version, pending과 동일한 버전에는 ok를 반환한다.", () -> {
                    //given
                    String clientVersion = "1.0.1";

                    //when
                    BDDMockito.given(appVersionRepository.findAndroidVersionInfo()).willReturn(appVersion);

                    //then
                    Assertions.assertThat(appVersionService.findAndroidVersionStatus(clientVersion)).isEqualTo(AppVersionStatus.OK);
                })
        );

    }
}