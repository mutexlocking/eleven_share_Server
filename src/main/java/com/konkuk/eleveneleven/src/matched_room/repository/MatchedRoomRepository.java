package com.konkuk.eleveneleven.src.matched_room.repository;

import com.konkuk.eleveneleven.src.matched_room.MatchedRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MatchedRoomRepository extends JpaRepository<MatchedRoom, Long> {


    @Query("select distinct mr from MatchedRoom mr join fetch mr.ownerMember m join fetch mr.matchedRoomMemberList where mr.idx = :matchedRoomIdx")
    Optional<MatchedRoom> findMatchedRoomFetch(@Param("matchedRoomIdx") Long matchedRoomIdx);


}
