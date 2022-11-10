package com.konkuk.eleveneleven.src.matched_room_member.repository;

import com.konkuk.eleveneleven.common.enums.Status;
import com.konkuk.eleveneleven.src.matched_room.MatchedRoom;
import com.konkuk.eleveneleven.src.matched_room_member.MatchedRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatchedRoomMemberRepository extends JpaRepository<MatchedRoomMember, Long> {

    Optional<MatchedRoomMember> findByMemberIdxAndStatus(Long memberIdx, Status status);

    boolean existsByMemberIdxAndStatus(Long memberIdx, Status status);

    boolean existsByMemberIdxAndMatchedRoomIdxAndStatus(Long memberIdx, Long MatchedRoomIdx, Status status);

    List<MatchedRoomMember> findAllByMatchedRoomAndStatus(MatchedRoom matchedRoom, Status status);
}
