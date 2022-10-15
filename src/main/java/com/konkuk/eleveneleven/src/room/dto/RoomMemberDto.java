package com.konkuk.eleveneleven.src.room.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomMemberDto {
    private String name;
    private String schoolName;
    private String major;
    private Boolean isOwner;

}
