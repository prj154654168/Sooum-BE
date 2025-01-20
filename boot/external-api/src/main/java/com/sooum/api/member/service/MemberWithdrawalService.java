package com.sooum.api.member.service;

import com.sooum.api.member.dto.AuthDTO;
import com.sooum.data.block.service.BlockMemberService;
import com.sooum.data.card.service.*;
import com.sooum.data.follow.service.FollowService;
import com.sooum.data.img.service.CardImgService;
import com.sooum.data.img.service.ProfileImgService;
import com.sooum.data.member.entity.Blacklist;
import com.sooum.data.member.entity.Member;
import com.sooum.data.member.entity.Role;
import com.sooum.data.member.service.*;
import com.sooum.data.notification.service.NotificationHistoryService;
import com.sooum.data.suspended.entity.Suspended;
import com.sooum.data.suspended.service.SuspendedService;
import com.sooum.data.tag.service.CommentTagService;
import com.sooum.data.tag.service.FavoriteTagService;
import com.sooum.data.tag.service.FeedTagService;
import com.sooum.data.visitor.service.VisitorService;
import com.sooum.global.config.jwt.RedisTokenPathPrefix;
import com.sooum.global.config.jwt.TokenProvider;
import com.sooum.global.config.jwt.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberWithdrawalService {
    private final MemberService memberService;
    private final SuspendedService suspendedService;
    private final TokenProvider tokenProvider;
    private final BlacklistService blacklistService;
    private final FeedCardService feedCardService;
    private final CommentCardService commentCardService;
    private final FeedLikeService feedLikeService;
    private final CommentLikeService commentLikeService;
    private final FavoriteTagService favoriteTagService;
    private final PopularFeedService popularFeedService;
    private final CardImgService cardImgService;
    private final FollowService followService;
    private final VisitorService visitorService;
    private final BlockMemberService blockMemberService;
    private final PolicyService policyService;
    private final RefreshTokenService refreshTokenService;
    private final ProfileImgService profileImgService;
    private final AccountTransferService accountTransferService;
    private final FeedTagService feedTagService;
    private final CommentTagService commentTagService;
    private final NotificationHistoryService notificationHistoryService;
    private final BlackListUseCase blackListUseCase;

    private final RedisTemplate<String, String> redisStringTemplate;

    @Transactional
    public void withdrawMember(Long memberPk, AuthDTO.Token token) throws InvalidTokenException {
        Member member = memberService.findMember(memberPk);

        handleSuspendedUser(member);
        addTokensToBlacklist(token);

        popularFeedService.deletePopularCardByMemberPk(memberPk);

        cardImgService.updateCardImgNull(memberPk);

        feedLikeService.deleteAllMemberLikes(memberPk);
        commentLikeService.deleteAllMemberLikes(memberPk);

        favoriteTagService.deleteAllFavoriteTag(memberPk);

        feedTagService.deleteFeedTag(memberPk);
        commentTagService.deleteCommentTag(memberPk);

        commentCardService.deleteCommentCardByMemberPk(memberPk);
        feedCardService.deleteFeedCardByMemberPk(memberPk);

        followService.deleteAllFollow(memberPk);
        visitorService.handleVisitorOnMemberWithdraw(memberPk);
        blockMemberService.deleteAllBlockMember(memberPk);

        policyService.deletePolicyTerm(memberPk);
        refreshTokenService.deleteRefreshToken(memberPk);
        accountTransferService.deleteAccountTransfer(memberPk);
        profileImgService.updateProfileImgNull(memberPk);
        notificationHistoryService.deleteAllNotificationHistory(memberPk);

        memberService.deleteMember(memberPk);
    }

    @Transactional
    public void withdrawMember(Long memberPk) {
        String refreshToken = refreshTokenService.findRefreshToken(memberPk);
        blackListUseCase.saveBlackListRefreshToken(refreshToken);

        popularFeedService.deletePopularCardByMemberPk(memberPk);

        cardImgService.updateCardImgNull(memberPk);

        feedLikeService.deleteAllMemberLikes(memberPk);
        commentLikeService.deleteAllMemberLikes(memberPk);

        favoriteTagService.deleteAllFavoriteTag(memberPk);

        feedTagService.deleteFeedTag(memberPk);
        commentTagService.deleteCommentTag(memberPk);

        commentCardService.deleteCommentCardByMemberPk(memberPk);
        feedCardService.deleteFeedCardByMemberPk(memberPk);

        followService.deleteAllFollow(memberPk);
        visitorService.handleVisitorOnMemberWithdraw(memberPk);
        blockMemberService.deleteAllBlockMember(memberPk);

        policyService.deletePolicyTerm(memberPk);
        refreshTokenService.deleteRefreshToken(memberPk);
        accountTransferService.deleteAccountTransfer(memberPk);
        profileImgService.updateProfileImgNull(memberPk);
        notificationHistoryService.deleteAllNotificationHistory(memberPk);

        memberService.deleteMember(memberPk);
    }

    private void handleSuspendedUser(Member member) {
        Suspended.SuspendedBuilder suspendedBuilder = Suspended.builder()
                .deviceId(member.getDeviceId());
        if (member.getRole().equals(Role.BANNED)) {
            suspendedBuilder
                    .untilBan(member.getUntilBan())
                    .isBanUser(true);
        } else {
            suspendedBuilder
                    .untilBan(LocalDateTime.now().plusDays(7))
                    .isBanUser(false);
        }
        suspendedService.save(suspendedBuilder.build());
    }

    private void addTokensToBlacklist(AuthDTO.Token token) throws InvalidTokenException {
        LocalDateTime accessTokenExpiredAt = tokenProvider.getExpirationAllowExpired(token.accessToken());
        LocalDateTime refreshTokenExpiredAt = tokenProvider.getExpirationAllowExpired(token.refreshToken());

        if (accessTokenExpiredAt.isAfter(LocalDateTime.now())) {
            redisStringTemplate.opsForValue().set(
                    RedisTokenPathPrefix.ACCESS_TOKEN.getPrefix() + token.accessToken(), "",
                    Duration.between(LocalDateTime.now(), accessTokenExpiredAt)
            );
        }

        if (refreshTokenExpiredAt.isAfter(LocalDateTime.now())) {
            redisStringTemplate.opsForValue().set(
                    RedisTokenPathPrefix.REFRESH_TOKEN.getPrefix() + token.refreshToken(), "",
                    Duration.between(LocalDateTime.now(), refreshTokenExpiredAt)
            );
        }

        List<Blacklist> blacklistList = List.of(
                Blacklist.builder()
                        .token(token.accessToken())
                        .expiredAt(accessTokenExpiredAt).build(),
                Blacklist.builder()
                        .token(token.refreshToken())
                        .expiredAt(refreshTokenExpiredAt).build()
        );
        blacklistService.saveAll(blacklistList);
    }
}
