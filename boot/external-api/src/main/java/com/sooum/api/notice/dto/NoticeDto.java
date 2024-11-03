package com.sooum.api.notice.dto;

import com.sooum.data.notice.entity.Notice;
import lombok.Builder;

@Builder
public record NoticeDto(Long id, String title, String link) {
    public static NoticeDto of(Notice notice) {
        return NoticeDto.builder()
                .id(notice.getPk())
                .title(notice.getTitle())
                .link(notice.getUrl())
                .build();
    }
}
