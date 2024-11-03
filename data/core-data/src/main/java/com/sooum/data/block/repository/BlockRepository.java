package com.sooum.data.block.repository;

import com.sooum.data.block.entity.Block;
import com.sooum.data.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BlockRepository extends JpaRepository<Block, Long> {
    @Query("select b.toMember.pk from Block b where b.fromMember.pk = :memberPk")
    List<Long> findAllBlockToPk(@Param("memberPk") Long memberPk);
    boolean existsByFromMemberPkAndToMemberPk(Long fromMemberPk, Long toMemberPk);

    @Modifying
    @Query("delete from Block b where b.fromMember = :fromMember and b.toMember = :toMember")
    void deleteBlockMember(@Param("fromMember") Member fromMember, @Param("toMember") Member toMember);
}
