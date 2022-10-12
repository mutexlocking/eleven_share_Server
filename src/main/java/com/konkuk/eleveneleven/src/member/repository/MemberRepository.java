package com.konkuk.eleveneleven.src.member.repository;

import com.konkuk.eleveneleven.common.enums.Status;
import com.konkuk.eleveneleven.src.member.Member;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByKakaoId(Long kakaoId);

    Member findByKakaoId(Long kakaoId);
    Member findByKakaoIdAndStatus(Long kakaoId, Status status);

    @Query("select m from Member m join fetch m.roomMember rm join fetch rm.room r where m.kakaoId=:kakaoId")
    Member findWithRoom(@Param("kakaoId") Long kakaoId);
}
