package com.konkuk.eleveneleven.src.room.vo;

import com.konkuk.eleveneleven.common.enums.Gender;
import com.konkuk.eleveneleven.src.room.Room;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RandMatchResult {
    private List<Long> failMatchRoomList;
    private List<Long> successMatchRoomList;

}
