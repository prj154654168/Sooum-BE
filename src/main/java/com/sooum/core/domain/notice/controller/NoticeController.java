package com.sooum.core.domain.notice.controller;

import com.sooum.core.domain.notice.dto.NoticeDto;
import com.sooum.core.domain.notice.service.NoticeService;
import com.sooum.core.global.responseform.ResponseCollectionModel;
import com.sooum.core.global.responseform.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    @GetMapping(value = {"","/{lastPk}"})
    public ResponseEntity<CollectionModel<NoticeDto>> findNoticesList(
            @PathVariable(required = false, value = "lastPk") Optional<Long> lastPk
    ) {
        List<NoticeDto> notices = noticeService.findNotices(lastPk.orElse(0L));
        if (notices.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(
                ResponseCollectionModel.<NoticeDto>builder()
                        .status(
                                ResponseStatus.builder()
                                        .httpCode(HttpStatus.OK.value())
                                        .httpStatus(HttpStatus.OK)
                                        .responseMessage("Notice list retrieved successfully")
                                        .build()
                        ).content(notices)
                        .build()
                        .add(
                                linkTo(this.getClass())
                                        .slash("/"+notices.get(notices.size() - 1).id())
                                        .withSelfRel()
                        )
        );
    }

}
