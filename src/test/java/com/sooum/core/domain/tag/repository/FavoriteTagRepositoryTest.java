package com.sooum.core.domain.tag.repository;

import com.sooum.core.domain.card.repository.FeedCardRepository;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.entity.devicetype.DeviceType;
import com.sooum.core.domain.member.repository.MemberRepository;
import com.sooum.core.domain.tag.entity.FavoriteTag;
import com.sooum.core.domain.tag.entity.Tag;
import com.sooum.core.global.config.redis.RedisConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(RedisConfig.class)
@MockBean(CachedTagRepository.class)
class FavoriteTagRepositoryTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    FeedCardRepository feedCardRepository;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    FavoriteTagRepository favoriteTagRepository;

    private static final String TAG = "테스트 태그";

    @Test
    @DisplayName("즐겨찾기 태그 존재")
    void isExistFavoriteTag() throws Exception{
        // given
        Member member = createMember();
        Tag tag = createTag();
        createFavoriteTag(tag, member);

        // when
        boolean isExist = favoriteTagRepository.existsByTag_PkAndMember_Pk(tag.getPk(), member.getPk());

        // then
        Assertions.assertThat(isExist).isTrue();
    }

    @Test
    @DisplayName("즐겨찾기 태그 존재x")
    void isNotExistFavoriteTag() throws Exception{
        // given
        Member member = createMember();

        // when
        boolean isExist = favoriteTagRepository.existsByTag_PkAndMember_Pk(1L, member.getPk());

        // then
        Assertions.assertThat(isExist).isFalse();
    }

    @Test
    @DisplayName("태그와 멤버로 즐겨찾기 태그 조회 성공_즐겨찾기 존재")
    void findFavoriteTag_Exist() throws Exception{
        // given
        Member member = createMember();
        Tag tag = createTag();
        createFavoriteTag(tag, member);

        // when
        Optional<FavoriteTag> findFavoriteTag = favoriteTagRepository.findByTag_PkAndMember_Pk(tag.getPk(), member.getPk());

        // then
        Assertions.assertThat(findFavoriteTag.isPresent()).isTrue();
    }

    @Test
    @DisplayName("태그와 멤버로 즐겨찾기 태그 조회 실패_즐겨찾기가 존재하지 않음")
    void findFavoriteTag_NotExist() throws Exception{
        // given
        Member member = createMember();
        Tag tag = createTag();

        // when
        Optional<FavoriteTag> findFavoriteTag = favoriteTagRepository.findByTag_PkAndMember_Pk(tag.getPk(), member.getPk());

        // then
        Assertions.assertThat(findFavoriteTag.isEmpty()).isTrue();
    }

    private Member createMember() {
        Member build1 = Member.builder().deviceId("dev")
                .deviceType(DeviceType.IOS)
                .firebaseToken("fire")
                .nickname("nick")
                .isAllowNotify(true)
                .build();
        return memberRepository.save(build1);
    }

    private Tag createTag() {
        Tag build = Tag.builder()
                .content(TAG)
                .build();
        return tagRepository.save(build);
    }

    private void createFavoriteTag(Tag tag, Member member) {
        FavoriteTag favoriteTag = FavoriteTag.builder()
                .member(member)
                .tag(tag)
                .build();
        favoriteTagRepository.save(favoriteTag);
    }
}