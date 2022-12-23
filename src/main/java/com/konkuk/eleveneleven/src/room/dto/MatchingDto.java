package com.konkuk.eleveneleven.src.room.dto;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchingDto {
    private List<MemberDto> memberDtoList;
    private Long roomIdx;
}
