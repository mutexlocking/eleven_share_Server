package com.konkuk.eleveneleven.src.room.repository;

import com.konkuk.eleveneleven.common.enums.Status;
import com.konkuk.eleveneleven.src.room.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("select distinct r from Room r join fetch r.roomMemberList rm join fetch rm.member where r.idx=:roomIdx")
    Room findAtRoomIdx(@Param("roomIdx")Long roomIdx);

    @Query("select distinct r from Room r join fetch r.roomMemberList rm join fetch rm.member where r.roomCode=:roomCode")
    Room findAtRoomCode(@Param("roomCode")String roomCode);

    boolean existsByRoomCodeAndStatus(String roomCode, Status status);

    Room findByRoomCode(String roomCode);

    @Query("select r from Room r where r.idx=:idx")
    Room findByIdx(@Param("idx")Long idx);
}
