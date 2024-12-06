package com.sooum.data.notice.entity;

import com.sooum.data.notice.entity.noticetype.NoticeType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice {

    @Id
    private Long pk;

    @Column(name = "title")
    private String title;

    @Column(name = "url")
    private String url;

    @Column(name = "date")
    private LocalDate date;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "noticeType")
    private NoticeType noticeType;


}
