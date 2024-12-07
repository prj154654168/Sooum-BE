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
import com.sooum.data.report.service.CommentReportService;
import com.sooum.data.report.service.FeedReportService;
import com.sooum.data.suspended.entity.Suspended;
import com.sooum.data.suspended.service.SuspendedService;
import com.sooum.data.tag.service.CommentTagService;
import com.sooum.data.tag.service.FavoriteTagService;
import com.sooum.data.tag.service.FeedTagService;
import com.sooum.data.visitor.service.VisitorService;
import com.sooum.global.config.jwt.InvalidTokenException;
import com.sooum.global.config.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final FeedReportService feedReportService;
    private final CommentReportService commentReportService;
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

        feedReportService.deleteAllFeedReports(memberPk);
        commentReportService.deleteAllCommentReports(memberPk);

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
        memberService.deleteMember(memberPk);
        //TODO: notification_history delete
    }

    private void handleSuspendedUser(Member member) {
        if (member.getRole().equals(Role.BANNED)) {
            Suspended suspended = Suspended.builder()
                    .deviceId(member.getDeviceId())
                    .untilBan(member.getUntilBan()).build();
            suspendedService.save(suspended);
        }
    }

    private void addTokensToBlacklist(AuthDTO.Token token) throws InvalidTokenException {
        tokenProvider.validateToken(token.accessToken());
        tokenProvider.validateToken(token.refreshToken());

        List<Blacklist> blacklistList = List.of(
                Blacklist.builder()
                        .token(token.accessToken())
                        .expiredAt(tokenProvider.getExpiration(token.accessToken())).build(),
                Blacklist.builder()
                        .token(token.refreshToken())
                        .expiredAt(tokenProvider.getExpiration(token.refreshToken())).build()
        );
        blacklistService.saveAll(blacklistList);
    }
}
