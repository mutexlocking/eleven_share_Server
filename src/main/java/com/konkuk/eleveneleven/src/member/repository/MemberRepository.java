package com.konkuk.eleveneleven.src.member.repository;

import com.konkuk.eleveneleven.common.enums.Status;
import com.konkuk.eleveneleven.src.member.Member;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByKakaoId(Long kakaoId);

    @Query("select m from Member m where m.kakaoId=:kakaoId")
    Member findByKakaoId(@Param("kakaoId") Long kakaoId);

    @Query("select m from Member m where m.kakaoId=:kakaoId")
    Optional<Member> findByKakaoIdOptional(@Param("kakaoId") Long kakaoId);

    Member findByKakaoIdAndStatus(Long kakaoId, Status status);

    @Query("select m from Member m join fetch m.roomMember rm join fetch rm.room r where m.kakaoId=:kakaoId")
    Member findWithRoom(@Param("kakaoId") Long kakaoId);

    @Query("select m from Member m join fetch m.roomMember rm join fetch rm.room r where m.kakaoId=:kakaoId")
    Optional<Member> findWithRoomOptional(@Param("kakaoId")Long kakaoId);
}
