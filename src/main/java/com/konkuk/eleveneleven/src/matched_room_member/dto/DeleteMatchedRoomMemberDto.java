package com.konkuk.eleveneleven.src.matched_room_member.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteMatchedRoomMemberDto {
    private Long memberIdx;
    private String name;
    private boolean owner;
    private Long matchedRoomIdx;
}
