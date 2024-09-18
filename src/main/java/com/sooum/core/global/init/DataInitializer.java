package com.sooum.core.global.init;

import com.sooum.core.domain.card.entity.FeedCard;
import com.sooum.core.domain.card.entity.font.Font;
import com.sooum.core.domain.card.entity.fontsize.FontSize;
import com.sooum.core.domain.card.entity.imgtype.ImgType;
import com.sooum.core.domain.card.repository.FeedCardRepository;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.entity.devicetype.DeviceType;
import com.sooum.core.domain.member.repository.MemberRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Component
public class DataInitializer {
    @Autowired
    private FeedCardRepository feedCardRepository;

    @Autowired
    private MemberRepository memberRepository;

    private final GeometryFactory geometryFactory = new GeometryFactory();

    public void init() {
        Member member = Member.builder()
                .deviceType(DeviceType.ANDROID)
                .nickname("abc")
                .deviceId("device-id")
                .isAllowNotify(true)
                .firebaseToken("adfadf").build();

        memberRepository.save(member);

        List<FeedCard> cards = new ArrayList<>();
        Optional<Member> byName = memberRepository.findByNickname("abc");
// 역 데이터 추가
        Object[][] stations = {
                {126.9052383, 37.5157702, "영등포역"},
                {126.8890174, 37.5088141, "신도림역"},
                {126.8927728, 37.4925085, "대림역"},
                {126.9347011, 37.5551399, "신촌역"},
                {126.9221228, 37.5215737, "여의도역"},
                {126.978379, 37.5665, "서울역"},  // 추가된 역 데이터
                {127.0276, 37.4981, "강남역"},    // 추가된 역 데이터
                {126.9787, 37.5666, "명동역"},    // 추가된 역 데이터
                {127.028, 37.502, "삼성역"},      // 추가된 역 데이터
                {126.9372, 37.5585, "홍대입구역"}  // 추가된 역 데이터
        };

        for (int i = 0; i < stations.length; i++) {
            double longitude = (double) stations[i][0];
            double latitude = (double) stations[i][1];
            String stationName = (String) stations[i][2];

            FeedCard card = FeedCard.builder()
                    .content(stationName)
                    .fontSize(FontSize.BIG)
                    .font(Font.CUSTOM)
                    .location(geometryFactory.createPoint(new Coordinate(longitude, latitude)))
                    .imgType(ImgType.DEFAULT)
                    .imgName("image")
                    .isPublic(true)
                    .isStory(i % 2 == 0)
                    .writer(byName.get())
                    .build();

            cards.add(card);
        }

        feedCardRepository.saveAll(cards);
    }

}
