package com.konkuk.eleveneleven.src.room_member.service;

import com.konkuk.eleveneleven.common.enums.Gender;
import com.konkuk.eleveneleven.config.BaseException;
import com.konkuk.eleveneleven.config.BaseResponseStatus;
import com.konkuk.eleveneleven.src.member.Member;
import com.konkuk.eleveneleven.src.member.repository.MemberRepository;
import com.konkuk.eleveneleven.src.room_member.repository.RoomMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomMemberService {

    private final RoomMemberRepository roomMemberRepository;

    /** DB상 RoomMember가 비어있는지 확인 */
    public void checkIsRoomMemberEmpty(){
        if (roomMemberRepository.findAll().size() == 0) {
            throw new BaseException(BaseResponseStatus.ROOM_MEMBER_EMPTY_IN_DB);
        }
    }

}
