package com.konkuk.eleveneleven.src.member_room.repository;

import com.konkuk.eleveneleven.src.member_room.MemberRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRoomRepository extends JpaRepository<MemberRoom, Long> {

    /** FK인 memberIdx로 MemberRoom을 조회하면서 -> 연관된 Room , 연관된 OwnerMember를 함께 조회해옴 */
    @Query("select mr from MemberRoom mr join fetch mr.room r join fetch r.ownerMember m where mr.member.idx = :memberIdx")
    Optional<MemberRoom> findByMemberIdx(@Param("memberIdx") Long memberIdx);
}
