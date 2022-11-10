package com.konkuk.eleveneleven.src.scheduler.controller;

import com.konkuk.eleveneleven.src.matched_room.service.MatchedRoomService;
import com.konkuk.eleveneleven.src.room.service.RoomMatchingService;
import com.konkuk.eleveneleven.src.room.service.RoomService;
import com.konkuk.eleveneleven.src.room_member.service.RoomMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class SchedulerController {

    private final RoomMatchingService roomMatchingService;
    private final RoomService roomService;
    private final RoomMemberService roomMemberService;
    private final MatchedRoomService matchedRoomService;

    /** 매일 23시에 Scheduler를 동작하여 matching 진행 */
    @PostMapping("/schedule/test")
    @Transactional
    public void matchRoom() {
        roomService.checkIsRoomEmpty();
        roomMemberService.checkIsRoomMemberEmpty();
        roomMatchingService.randMatchRoom();
        matchedRoomService.migrateRoomToMatchedRoom();
        roomService.deleteAllRoomMemberInDB();
        roomService.deleteAllRoomInDB();
    }
}
