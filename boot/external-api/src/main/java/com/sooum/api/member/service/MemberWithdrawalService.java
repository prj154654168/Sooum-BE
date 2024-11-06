package com.sooum.api.member.service;

import com.sooum.data.card.entity.FeedCard;
import com.sooum.data.card.service.FeedCardService;
import com.sooum.data.member.entity.Blacklist;
import com.sooum.data.member.entity.Member;
import com.sooum.data.member.entity.Role;
import com.sooum.data.member.service.BlacklistService;
import com.sooum.data.member.service.MemberService;
import com.sooum.data.suspended.entity.Suspended;
import com.sooum.data.suspended.service.SuspendedService;
import com.sooum.global.config.jwt.InvalidTokenException;
import com.sooum.global.config.jwt.TokenProvider;
import com.sun.net.httpserver.HttpServer;
import jakarta.servlet.http.HttpServletRequest;
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

    @Transactional
    public void withdrawMember(Long memberPk, HttpServletRequest request) {
        Member member = memberService.findByPk(memberPk);

        if (member.getRole().equals(Role.BANNED)) {
            Suspended suspended = Suspended.builder()
                    .deviceId(member.getDeviceId())
                    .untilBan(member.getUntilBan()).build();

            suspendedService.save(suspended);
        }

        String accessToken = tokenProvider.getAccessToken(request)
                .orElseThrow(InvalidTokenException::new);
        Blacklist blacklist = Blacklist.builder()
                .token(accessToken)
                .expiredAt(tokenProvider.getExpiration(accessToken)).build();

        blacklistService.save(blacklist);



        List<FeedCard> feedCards = feedCardService.findAllFeedList(memberPk);
        for (FeedCard feedCard : feedCards) {
            feedCard.setWriter(null); // 또는 다른 적절한 처리
        }
        feedCardService.saveAllFeedCard();

        List<CommentCard> commentCards = commentCardRepository.findByWriter(member);
        for (CommentCard commentCard : commentCards) {
            commentCard.setWriter(null); // 또는 다른 적절한 처리
        }
        commentCardRepository.saveAll(commentCards); // 저장
    }
}
