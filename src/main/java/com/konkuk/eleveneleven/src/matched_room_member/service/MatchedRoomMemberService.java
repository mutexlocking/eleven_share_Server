package com.konkuk.eleveneleven.src.matched_room_member.service;

import com.konkuk.eleveneleven.common.enums.Gender;
import com.konkuk.eleveneleven.common.enums.Status;
import com.konkuk.eleveneleven.src.matched_room.MatchedRoom;
import com.konkuk.eleveneleven.src.matched_room_member.MatchedRoomMember;
import com.konkuk.eleveneleven.src.matched_room_member.dto.GetMatchedRoomMemberRes;
import com.konkuk.eleveneleven.src.matched_room_member.repository.MatchedRoomMemberRepository;
import com.konkuk.eleveneleven.src.member.Member;
import com.konkuk.eleveneleven.src.member.repository.MemberRepository;
import com.konkuk.eleveneleven.src.room_member.repository.RoomMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchedRoomMemberService {

    private final MemberRepository memberRepository;
    private final MatchedRoomMemberRepository matchedRoomMemberRepository;

    public List<Member> getMatchedRoomMember(Long kakaoId){
        Long memberIdx = getMemberIdx(kakaoId);
        MatchedRoom matchedRoom = getMatchedRoomIdx(memberIdx);
        Gender memberGender = getMemberGender(kakaoId);

        List<MatchedRoomMember> allByMatchedRoomAndStatus = matchedRoomMemberRepository.findAllByMatchedRoomAndStatus(matchedRoom, Status.ACTIVE);

        return allByMatchedRoomAndStatus.stream()
                .filter(mrm -> !mrm.getMember().getGender().equals(memberGender))
                .map(mrm -> mrm.getMember())
                .collect(Collectors.toList());
    }

    private Long getMemberIdx(Long kakaoId){
        return memberRepository.findByKakaoId(kakaoId).getIdx();
    }
    private MatchedRoom getMatchedRoomIdx(Long memberIdx){
        return matchedRoomMemberRepository.findByMemberIdxAndStatus(memberIdx, Status.ACTIVE).get().getMatchedRoom();
    }

    private Gender getMemberGender(Long kakakId){
        Member memberByKakaoId = memberRepository.findByKakaoId(kakakId);
        return memberByKakaoId.getGender();
    }
}
