package com.sooum.data.member.repository;

import com.sooum.data.member.entity.Blacklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlacklistRepository extends JpaRepository<Blacklist, String> {
}
