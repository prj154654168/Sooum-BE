package com.sooum.core.domain.tag.entity;

import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;
import com.redis.om.spring.annotations.Searchable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@Document
@RequiredArgsConstructor
@AllArgsConstructor
public class CachedTag {

    @Id @Searchable
    private String content;

    @Indexed
    private Integer count;
}
