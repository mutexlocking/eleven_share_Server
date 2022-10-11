package com.konkuk.eleveneleven.src.room_member.repository;

import com.konkuk.eleveneleven.src.room_member.RoomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoomMemberRepository extends JpaRepository<RoomMember, Long> {

    /** FK인 memberIdx로 MemberRoom을 조회하면서 -> 연관된 Room , 연관된 OwnerMember를 함께 조회해옴 */
    @Query("select mr from RoomMember mr join fetch mr.room r join fetch r.ownerMember m where mr.member.idx = :memberIdx")
    Optional<RoomMember> findByMemberIdx(@Param("memberIdx") Long memberIdx);
}
