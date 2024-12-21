package com.sooum.batch.blacklist.repository;

import com.sooum.data.member.repository.BlacklistRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface BlacklistBatchRepository extends BlacklistRepository {
    @Modifying
    @Transactional
    @Query("delete from Blacklist b where b.token in :tokens")
    void deleteAllByTokenIn(@Param("tokens") Iterable<String> tokens);
}