package com.konkuk.eleveneleven.src.matched_room.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UrlDto {


    private Long matchedRoomIdx;
    private String url;
}
