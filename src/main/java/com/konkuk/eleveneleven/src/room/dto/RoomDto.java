package com.konkuk.eleveneleven.src.room.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDto {

    private Long roomIdx;
    private String roomCode;
    private Long ownerMemberIdx;
}
