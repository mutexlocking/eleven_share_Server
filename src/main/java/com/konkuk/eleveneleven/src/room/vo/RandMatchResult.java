package com.konkuk.eleveneleven.src.room.vo;

import com.konkuk.eleveneleven.common.enums.Gender;
import com.konkuk.eleveneleven.src.room.Room;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class RandMatchResult {
    private Map<Gender, List<Room>> failMatchRoomMap;
    private List<Room> successMatchRoomMale;
    private List<Room> successMatchRoomFemale;

    @Builder
    public RandMatchResult(Map<Gender, List<Room>> failMatchRoomMap, List<Room> successMatchRoomMale, List<Room> successMatchRoomFemale) {
        this.failMatchRoomMap = failMatchRoomMap;
        this.successMatchRoomMale = successMatchRoomMale;
        this.successMatchRoomFemale = successMatchRoomFemale;
    }
}
