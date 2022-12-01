package com.konkuk.eleveneleven.src.matched_room_member.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchingMember {
    private String memberName;
    private String schoolName;
    private String major;
    private boolean isOwner;
}
