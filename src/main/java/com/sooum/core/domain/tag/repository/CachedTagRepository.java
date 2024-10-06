package com.sooum.core.domain.tag.repository;

import com.redis.om.spring.repository.RedisDocumentRepository;
import com.sooum.core.domain.tag.entity.CachedTag;

import java.util.List;

public interface CachedTagRepository extends RedisDocumentRepository<CachedTag, String> {

    List<CachedTag> findTop5ByContentLikeIgnoreCaseOrderByCountDesc(String content);
}
