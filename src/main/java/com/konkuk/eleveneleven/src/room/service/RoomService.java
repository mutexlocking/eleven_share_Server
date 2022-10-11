package com.konkuk.eleveneleven.src.room.service;

import com.konkuk.eleveneleven.common.enums.Status;
import com.konkuk.eleveneleven.config.BaseException;
import com.konkuk.eleveneleven.config.BaseResponseStatus;
import com.konkuk.eleveneleven.src.member.Member;
import com.konkuk.eleveneleven.src.member.repository.MemberRepository;
import com.konkuk.eleveneleven.src.room.Room;
import com.konkuk.eleveneleven.src.room.dto.RoomDto;
import com.konkuk.eleveneleven.src.room.dto.RoomMemberDto;
import com.konkuk.eleveneleven.src.room.repository.RoomRepository;
import com.konkuk.eleveneleven.src.room_member.RoomMember;
import com.konkuk.eleveneleven.src.room_member.repository.RoomMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomService {

    private final MemberRepository memberRepository;
    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;

    private RoomDto saveRoom(Member ownerMember){
        //1. 인증코드 생성
        String roomCode = UUID.randomUUID().toString().substring(0, 6);

        //2. Room 엔티티 생성 후 , DB에 save
        Room room = new Room(ownerMember, roomCode);
        roomRepository.save(room);

        //3. 이후 Room 엔티티로부터 필요한 정보를 추출하여 RoomDto로 반환
        return RoomDto.builder()
                .roomIdx(room.getIdx())
                .ownerMemberIdx(ownerMember.getIdx())
                .roomCode(roomCode)
                .build();
    }

    private void saveRoomMember(Long ownerMemberIdx, Long roomIdx){

        //1. ownerMember와 , 생성된 Room을 조회
        Member ownerMember = memberRepository.findById(ownerMemberIdx).orElseThrow(
                () -> {
                    throw new BaseException(BaseResponseStatus.INVALID_OWNER_MEMBER, "방만들기 진행 시 : 방장에 대응되는 RoomMember를 생성하는 과정에서 , 방장인 Member를 repository에서 idx로 조회해 올 떄 , idx가 유효하지 않은 값이어서 조회가 안되는 예외가 발생 ");
                }
        );

        Room room = roomRepository.findById(roomIdx).orElseThrow(
                () -> {
                    throw new BaseException(BaseResponseStatus.INVALID_ROOM, "방만들기 진행 시 : 방을 만들고난 이후 방장에 대응되는 RoomMember를 생성하는 과정에서 , 만들어진 방인 Room을 repository에서 idx로 조회해 올 때, idx가 유효하지 않은 값이어서 조회가 안되는 예되 발생");
                }
        );

        //2. 방장에 대응되는 RoomMember 생성 하여 DB에 insert
        RoomMember owner = new RoomMember(room, ownerMember);
        roomMemberRepository.save(owner);

    }

    /** 검증 로직 : 인증을 다 마치지 않은 , ACTIVE가 아닌 사용자가 방을 만들려고 하면 -> 예외를 터뜨릴 것 */
    private void checkMemberStatus(Member ownerMember){
        if(ownerMember.getStatus()!= Status.ACTIVE){
            throw new BaseException(BaseResponseStatus.INVALID_MEMBER, "방만들기 진행 시 : 방을 만들기 전 , 방을 만들려고 하는 방장 사용자의 인증 상태를 검사하였는데, 아직 모든 인증이 다 진행되지 않은 ONGOING 상태이다.");
        }
    }

    @Transactional
    public RoomDto createRoom(Long kakaoId){

        //0. kakaoId로 방장인 Member 조회 (kakaoId에 대한 유효성 검사는 끝난 상태 )
        Member ownerMember = memberRepository.findByKakaoId(kakaoId);

        //1. 그 방을 만들려는 Member가 , 1차 2차 인증과정을 모두 마쳐서 상태가 status 가 되었는지를 check
        checkMemberStatus(ownerMember);

        //2. Room Entity 생성하여 save
        RoomDto roomDto = saveRoom(ownerMember);

        //3. 그 Room에 속한 방장인 RoomMember Entity 생성하여 save
        saveRoomMember(roomDto.getOwnerMemberIdx(), roomDto.getRoomIdx());

        //4. 이들로 부터 적절한 값을 추출하여 RoomDto를 생성하여 반환
        return roomDto;
    }
}
