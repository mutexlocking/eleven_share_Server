package com.konkuk.eleveneleven.src.matched_room_member.controller;

import com.konkuk.eleveneleven.src.matched_room_member.dto.GetMatchedRoomMemberRes;
import com.konkuk.eleveneleven.src.matched_room_member.service.MatchedRoomMemberService;
import com.konkuk.eleveneleven.src.matched_room_member.vo.MatchingMember;
import com.konkuk.eleveneleven.src.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "matched/room")
public class MatchedRoomMemberController {
    private final MatchedRoomMemberService matchedRoomMemberService;

    /** [매칭 후 매칭된 방의 멤버들 확인 API] */
    @GetMapping("/member")
    public GetMatchedRoomMemberRes getMatchedRoomMember(@RequestAttribute Long kakaoId) {
        List<Member> matchedRoomMembers = matchedRoomMemberService.getMatchedRoomMember(kakaoId);

        return getMatchedRoomMemberDto(matchedRoomMembers);
    }

    private GetMatchedRoomMemberRes getMatchedRoomMemberDto(List<Member> matchedRoomMembers){
        List<MatchingMember> matchingMembers = new ArrayList<>();

        for (Member matchedRoomMember : matchedRoomMembers){
            matchingMembers.add(MatchingMember.builder()
                    .memberName(matchedRoomMember.getName())
                    .schoolName(matchedRoomMember.getSchoolName())
                    .major(matchedRoomMember.getMajor())
                    .build());
        }

        return GetMatchedRoomMemberRes.builder()
                .matchingMembers(matchingMembers)
                .build();
    }

}
