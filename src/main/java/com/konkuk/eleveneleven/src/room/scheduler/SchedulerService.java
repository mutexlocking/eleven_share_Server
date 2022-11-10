package com.konkuk.eleveneleven.src.room.scheduler;


import com.konkuk.eleveneleven.src.matched_room.service.MatchedRoomService;
import com.konkuk.eleveneleven.src.room.service.RoomMatchingService;
import com.konkuk.eleveneleven.src.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerService {

    private final RoomMatchingService roomMatchingService;
    private final RoomService roomService;
    private final MatchedRoomService matchedRoomService;

    /** 매일 23시에 Scheduler를 동작하여 matching 진행 */
    @Transactional
//    @Scheduled(cron = "0 11 23 * * ?")
//    @Scheduled(cron = "0 32 19 * * ?")
    public void matchRoom() {
        roomMatchingService.randMatchRoom();
        matchedRoomService.migrateRoomToMatchedRoom();
        roomService.deleteAllRoomMemberInDB();
        roomService.deleteAllRoomInDB();
    }
}
