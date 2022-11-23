package com.konkuk.eleveneleven.src.matched_room_member.dto;

import com.konkuk.eleveneleven.src.matched_room_member.vo.MatchingMember;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetMatchedRoomMemberRes {
    private long matchedRoomIdx;
    private List<MatchingMember> matchingMembers;
}
