package com.konkuk.eleveneleven.src.matched_room_member.service;

import com.konkuk.eleveneleven.common.enums.Gender;
import com.konkuk.eleveneleven.common.enums.Status;
import com.konkuk.eleveneleven.config.BaseException;
import com.konkuk.eleveneleven.config.BaseResponseStatus;
import com.konkuk.eleveneleven.src.matched_room.MatchedRoom;
import com.konkuk.eleveneleven.src.matched_room.repository.MatchedRoomRepository;
import com.konkuk.eleveneleven.src.matched_room_member.MatchedRoomMember;
import com.konkuk.eleveneleven.src.matched_room_member.dto.DeleteMatchedRoomMemberDto;
import com.konkuk.eleveneleven.src.matched_room_member.repository.MatchedRoomMemberRepository;
import com.konkuk.eleveneleven.src.member.Member;
import com.konkuk.eleveneleven.src.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchedRoomMemberService {

    private final MemberRepository memberRepository;
    private final MatchedRoomMemberRepository matchedRoomMemberRepository;
    private final MatchedRoomRepository matchedRoomRepository;

    public List<Member> getMatchedRoomMember(Long memberIdx){
        MatchedRoom matchedRoom = getMatchedRoom(memberIdx);
        Gender memberGender = getMemberGender(memberIdx);

        List<MatchedRoomMember> allByMatchedRoomAndStatus = matchedRoomMemberRepository.findAllByMatchedRoomAndStatus(matchedRoom, Status.ACTIVE);

        return allByMatchedRoomAndStatus.stream()
                .filter(mrm -> !mrm.getMember().getGender().equals(memberGender))
                .map(mrm -> mrm.getMember())
                .collect(Collectors.toList());
    }

    public MatchedRoom getMatchedRoom(Long memberIdx){
        Optional<MatchedRoomMember> byMemberIdxAndStatus = matchedRoomMemberRepository.findByMemberIdxAndStatus(memberIdx, Status.ACTIVE);

        matchFailValidation(byMemberIdxAndStatus);

        return byMemberIdxAndStatus.get().getMatchedRoom();
    }

    public boolean isOwnerMember(Long memberIdx) {
        Member byMemberIdx = memberRepository.findByMemberIdx(memberIdx);
        if(byMemberIdx.getMatchedRoom() != null) {
            return true;
        }
        return false;
    }

    private void matchFailValidation(Optional<MatchedRoomMember> byMemberIdxAndStatus){
        if(byMemberIdxAndStatus.isEmpty()) {
            throw new BaseException(BaseResponseStatus.FAIL_TO_MATCHING);
        }
    }

    private Gender getMemberGender(Long memberIdx){
        Member memberByKakaoId = memberRepository.findByMemberIdx(memberIdx);
        return memberByKakaoId.getGender();
    }


    /**---------------------------------------------------------------------- */
    /** [MathcedRoom에서 나가는 API ]*/
    public DeleteMatchedRoomMemberDto goOutMatchedRoom(Long memberIdx, Long matchedRoomIdx){

        Member member = memberRepository.findByMemberIdx(memberIdx);

        //1. 그 MathcedRoom에 그 Member가 속해있는지 검사
        MatchedRoomMember matchedRoomMember = matchedRoomMemberRepository.findByMemberIdxAndMatchedRoomIdxAndStatus(memberIdx, matchedRoomIdx, Status.ACTIVE)
                .orElseThrow(
                        () -> {
                            throw new BaseException(BaseResponseStatus.IS_NOT_BELEONG_TO_MATCHED_ROOM, "matched room 삭제 시점 : 방을 나가려는 해당 사용자는 , 사실 해당 MathcedRoom에 속한 사용자가 아닙니다.");
                        }
                );

        boolean isOwner = matchedRoomMember.getMatchedRoom().getOwnerMember().getIdx().equals(memberIdx);


        //2_1. 방장인 경우 방까지 삭제
        if(isOwner){

            MatchedRoom matchedRoom = matchedRoomRepository.findById(matchedRoomIdx).orElseThrow(
                    () -> {
                        throw new BaseException(BaseResponseStatus.ALREADY_DELETE_MATCHED_ROOM, "matched room 삭제 시점 : 해당 matched Room은 이미 삭제되었습니다.");
                    }
            );

            matchedRoom.getMatchedRoomMemberList().stream().forEach(mrm -> matchedRoomMemberRepository.delete(mrm));
           matchedRoomRepository.delete(matchedRoom);
        }
        else{
            //2_2. 방장이 아닌 경우 그 RoomMember만 delete
            matchedRoomMemberRepository.delete(matchedRoomMember);

        }

        //3. 리턴
        return DeleteMatchedRoomMemberDto.builder()
                .memberIdx(memberIdx)
                .name(member.getName())
                .owner(isOwner)
                .matchedRoomIdx(isOwner ? -1L : matchedRoomIdx)
                .build();
    }



}
