package com.sooum.core.domain.notice.service;

import com.sooum.core.domain.notice.dto.NoticeDto;
import com.sooum.core.domain.notice.entity.Notice;
import com.sooum.core.domain.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public List<NoticeDto> findNotices(Long lastPk) {
        List<Notice> noticeList = noticeRepository.findNotice(lastPk, PageRequest.ofSize(30));
        return noticeList.stream().map(NoticeDto::of).toList();
    }
}
