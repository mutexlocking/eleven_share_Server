package com.konkuk.eleveneleven.src.member.repository;

import com.konkuk.eleveneleven.src.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
