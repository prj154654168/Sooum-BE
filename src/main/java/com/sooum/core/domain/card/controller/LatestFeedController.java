package com.sooum.core.domain.card.controller;

import com.sooum.core.domain.card.dto.FeedCardDto;
import com.sooum.core.domain.card.dto.LatestFeedCardDto;
import com.sooum.core.domain.card.service.LatestFeedService;
import com.sooum.core.domain.member.entity.Member;
import com.sooum.core.domain.member.repository.MemberRepository;
import com.sooum.core.global.responseform.ResponseEntityModel;
import com.sooum.core.global.responseform.ResponseStatus;
import com.sooum.core.global.util.NextPageLinkGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/cards/home")
public class LatestFeedController {

    private final LatestFeedService latestFeedService;
    private final MemberRepository memberRepository;

    @GetMapping(value = {"/latest","/latest/{last}","/latest/{last}/{latitude}/{longitude}"})
    public ResponseEntity<?> getLatestFeed(@PathVariable(required = false, value = "last") Optional<Long> last,
                                           @PathVariable(required = false, value = "latitude") Optional<Double> latitude,
                                           @PathVariable(required = false, value = "longitude") Optional<Double> longitude) { //todo memberId 추가해야함

        List<Member> all = memberRepository.findAll();//todo param memberId 추가되면 삭제

        List<LatestFeedCardDto> latestFeedInfo = latestFeedService.createLatestFeedInfo(last.orElse(0L), all.get(0).getPk(), latitude, longitude);

        if (latestFeedInfo.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        ResponseStatus status = ResponseStatus.builder().httpStatus(HttpStatus.OK).httpCode(HttpStatus.OK.value()).responseMessage("Retrieve latest feed data").build();


        Link nextPageLink = new NextPageLinkGenerator<LatestFeedCardDto>().generateNextPageLink(latestFeedInfo);

        List<LatestFeedCardDto> latestFeedCardDtos = new NextPageLinkGenerator<LatestFeedCardDto>().appendEachCardDetailLink(latestFeedInfo);

        ResponseEntityModel<LatestFeedCardDto> build = ResponseEntityModel.<LatestFeedCardDto>builder().status(status).content(latestFeedCardDtos).build();
        build.add(nextPageLink);

        return ResponseEntity.ok(build);
    }


}