package com.konkuk.eleveneleven.src.room.repository;

import com.konkuk.eleveneleven.common.enums.Gender;
import com.konkuk.eleveneleven.common.enums.Status;
import com.konkuk.eleveneleven.src.room.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("select distinct r from Room r join fetch r.roomMemberList rm join fetch rm.member where r.idx=:roomIdx")
    Room findAtRoomIdx(@Param("roomIdx")Long roomIdx);

    @Query("select distinct r from Room r join fetch r.roomMemberList rm join fetch rm.member where r.roomCode=:roomCode")
    Room findAtRoomCode(@Param("roomCode")String roomCode);

    boolean existsByRoomCodeAndStatus(String roomCode, Status status);

    Room findByRoomCodeAndStatus(String roomCode, Status status);

    @Query("select r from Room r where r.idx=:idx")
    Room findByIdx(@Param("idx")Long idx);

    List<Room> findByGenderAndStatus(Gender gender, Status status);

    @Modifying
    @Query("delete from Room r where r.ownerMember.idx=:ownerMemberIdx")
    void deleteByMemberIdx(@Param("ownerMemberIdx") Long ownerMemberIdx);

    List<Room> findAllByStatus(Status status);

}
