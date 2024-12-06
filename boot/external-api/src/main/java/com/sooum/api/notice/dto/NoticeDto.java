package com.sooum.api.notice.dto;

import com.sooum.data.notice.entity.Notice;
import com.sooum.data.notice.entity.noticetype.NoticeType;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record NoticeDto(Long id, NoticeType noticeType, LocalDate noticeDate, String title, String link) {
    public static NoticeDto of(Notice notice) {
        return NoticeDto.builder()
                .id(notice.getPk())
                .noticeType(notice.getNoticeType())
                .noticeDate(notice.getDate())
                .title(notice.getTitle())
                .link(notice.getUrl())
                .build();
    }
}
