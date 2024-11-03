package com.sooum.api.notice.service;

import com.sooum.api.notice.dto.NoticeDto;
import com.sooum.data.notice.entity.Notice;
import com.sooum.data.notification.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeUseCase {
    private final NoticeService noticeService;

    public List<NoticeDto> findNotices(Long lastPk) {
        List<Notice> noticeList = noticeService.findNotices(lastPk);
        return noticeList.stream().map(NoticeDto::of).toList();
    }
}
