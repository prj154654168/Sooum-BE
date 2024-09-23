package com.sooum.core.domain.block.repository;

import com.sooum.core.domain.block.entity.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BlockRepository extends JpaRepository<Block, Long> {
    @Query("select b.toMember.pk from Block b where b.fromMember.pk = :memberPk")
    List<Long> findAllBlockToPk(@Param("memberPk") Long memberPk);
    boolean existsByFromMemberPkAndToMemberPk(Long fromMemberPk, Long toMemberPk);
}
