package com.konkuk.eleveneleven.src.room.service;

import com.konkuk.eleveneleven.src.chat.vo.ChatRoom;
import com.konkuk.eleveneleven.src.room.repository.RoomRepository;
import com.konkuk.eleveneleven.src.room.vo.WaitingRoom;
import com.konkuk.eleveneleven.common.enums.Status;
import com.konkuk.eleveneleven.config.BaseException;
import com.konkuk.eleveneleven.config.BaseResponseStatus;
import com.konkuk.eleveneleven.src.member.Member;
import com.konkuk.eleveneleven.src.member.repository.MemberRepository;
import com.konkuk.eleveneleven.src.room.Room;
import com.konkuk.eleveneleven.src.room.dto.MemberDto;
import com.konkuk.eleveneleven.src.room.dto.RoomDto;
import com.konkuk.eleveneleven.src.room.repository.RoomRepository;
import com.konkuk.eleveneleven.src.room_member.RoomMember;
import com.konkuk.eleveneleven.src.room_member.repository.RoomMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomService {

    private final EntityManager em;
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

    private Long saveRoomMember(Long ownerMemberIdx, Long roomIdx){

        //1. ownerMember와 ,
        Member ownerMember = memberRepository.findById(ownerMemberIdx).orElseThrow(
                () -> {
                    throw new BaseException(BaseResponseStatus.INVALID_OWNER_MEMBER, "방만들기 진행 시 : 방장에 대응되는 RoomMember를 생성하는 과정에서 , 방장인 Member를 repository에서 idx로 조회해 올 떄 , idx가 유효하지 않은 값이어서 조회가 안되는 예외가 발생 ");
                }
        );
        //생성된 Room을 조회
        Room room = roomRepository.findById(roomIdx).orElseThrow(
                () -> {
                    throw new BaseException(BaseResponseStatus.INVALID_ROOM, "방만들기 진행 시 : 방을 만들고난 이후 방장에 대응되는 RoomMember를 생성하는 과정에서 , 만들어진 방인 Room을 repository에서 idx로 조회해 올 때, idx가 유효하지 않은 값이어서 조회가 안되는 예되 발생");
                }
        );

        //2. 방장에 대응되는 RoomMember 생성 하여 DB에 insert
        RoomMember owner = new RoomMember(room, ownerMember);
        roomMemberRepository.save(owner);

        return owner.getIdx();

    }

    /** 검증 로직 : 인증을 다 마치지 않은 , ACTIVE가 아닌 사용자가 방을 만들려고 하면 -> 예외를 터뜨릴 것 */
    private void checkMemberStatus(Member ownerMember){
        if(ownerMember.getStatus()!= Status.ACTIVE){
            throw new BaseException(BaseResponseStatus.INVALID_MEMBER, "방만들기 진행 시 : 방을 만들기 전 , 방을 만들려고 하는 방장 사용자의 인증 상태를 검사하였는데, 아직 모든 인증이 다 진행되지 않은 ONGOING 상태이다. or 심지어 비활성화된 INACTIVE 일수도 있다.");
        }
    }

    @Transactional
    public RoomDto createRoom(Long kakaoId){

        //0. kakaoId로 방장인 Member 조회 (kakaoId에 대한 유효성 검사는 끝난 상태 -> 하지만 모든 인증을 거쳐 ACTIVE 상태가 되었는지는 비확인)
        Member ownerMember = memberRepository.findByKakaoId(kakaoId);

        //1. 그 방을 만들려는 Member가 , 1차 2차 인증과정을 모두 마쳐서 상태가 status 가 되었는지를 check
        checkMemberStatus(ownerMember);
        //1_2. 그리고 이미 이 사람이 특정 방에 속해있는지 검사 -> 참여자로든 or 방장으로든 어떤 방에 속해있다면 , 새로운 방을 만들 수 없음
        checkBelongAnotherRoom(ownerMember);

        //2. Room Entity 생성하여 save
        RoomDto roomDto = saveRoom(ownerMember);

        //3. 그 Room에 속한 방장인 RoomMember Entity 생성하여 save
        saveRoomMember(roomDto.getOwnerMemberIdx(), roomDto.getRoomIdx());

        //4. 이들로 부터 적절한 값을 추출하여 RoomDto를 생성하여 반환
        return roomDto;
    }


    /** roomIdx를 가지고 , 그 Room에 참여한 모든 RoomMember들을 함꼐 조회해 와서 , dto 리스트로 반환해주는 서비스*/
    public List<MemberDto> getMembers(Long roomIdx){

        //1. 이 ROOM에 연관된 RoomMember:Member 들을 모두 함꼐 조회해옴
        Room room = roomRepository.findAtRoomIdx(roomIdx);

        //2. 이 room에 속한 RoomMemberList들을 createdAt기준으로 오름차순 정렬시켜서 (그러면 방장이 무조건 맨 앞)
        // 그대로 대응되는 Member에 접근하여 정보를 빼와서 MemberDto에 정보를 담아 리턴
        return room.getRoomMemberList().stream()
                .filter(rm -> rm.getStatus()==Status.ACTIVE)
                .sorted((rm1, rm2) -> rm1.getCreatedAt().compareTo(rm2.getCreatedAt()))
                .map(rm -> rm.getMember())
                .map(m -> MemberDto.builder().name(m.getName()).schoolName(m.getSchoolName()).major(m.getMajor()).build())
                .collect(Collectors.toList());


    }


    /** 여기서 검증해야할 껀 -> 참여할 수 있는 방이 있냐 ? 없냐 ?
     * 방에 삭제되었건
     * 방 참여 코드가 유효하지 않건
     * 여기서는 해당 방코드로 식별 가능한 ACTIVE한 방이 있냐 없냐!!
     * 있으면 해당 roomCode는 유효하다!!!!!!!!!!!!!!!!!!!!!!
     *
     * 그럼에도 클라이언트 입장에서는 방이 섹제된 경우를 구분 해 줄 필요가 있다고 판단하여 한 번 더 쿼리를 날린다! */
    private void checkRoomCode(String roomCode){


        if(roomRepository.existsByRoomCodeAndStatus(roomCode,Status.ACTIVE)==false){
            throw new BaseException(BaseResponseStatus.INVALID_ROOM_CODE, "방 참여시 발생 : 방 참여코드가 유효하지 않아 특정 방에 참여 불가");
        }
    }

    private void checkBelongAnotherRoom(Member member){

        /** 여기서 ACITVE가 안들어가주면 , 과거의 참여한 RoomMember일 수 있으니 , 현재 활성화된 RoomMember로 참여하고 있는지를 check! */
        if(roomMemberRepository.existsByMemberIdxAndStatus(member.getIdx(), Status.ACTIVE)){
            throw new BaseException(BaseResponseStatus.BELONG_TO_ANOTHER_ROOM, "방 생성 or 참여시 발생 : 방에 참여하려는 사용자가 , 현재 이미 다른 방에 소속되어 있기 때문에, 더이상 다른 방에 참여할 수 or 만들 수 없습니다.");
        }
    }



    private Long belongToRoom(Member member, Room room){

        RoomMember roomMember = new RoomMember(room, member);
        roomMemberRepository.save(roomMember);
        return roomMember.getIdx();
    }

    private List<MemberDto> getAllRoomMembers(Room room){
        /** 주의할 점은 room과 연관된 RoomMember들이 하나도 없을 수 있다면 -> 이에따라 null pointer exception이 발생할 수 있는데
         * 우리의 경우는 , 방을 생성하자 마자 방장이라는 RoomMember가 생기니깐 -> 그런 일은 일어나지 않고 -> 따라서 대비하지 않아도 됨 */
        return room.getRoomMemberList()
                .stream()
                .filter(rm -> rm.getStatus()==Status.ACTIVE) // 즉 현재 참여하고 있는 RoomMember에 한하여
                .map(rm -> rm.getMember())
                .map(m -> MemberDto.builder().name(m.getName()).schoolName(m.getSchoolName()).major(m.getMajor()).build())
                .collect(Collectors.toList());
    }


    /** 특정 방 참여코드를 통해 특정 방에 참여하는 서비스 */
    @Transactional
    public List<MemberDto> createRoomMember(Long kakaoId, String roomCode){

        //1. 방 참여코드및 중복 방 참여에 대한 유효성 검증 -> 1차 : 유효한 방은 있냐 ? / 2차 : 사용자는 방에 참여할 자격이 되냐?
        checkRoomCode(roomCode);        /** 방 참여코드가 유효한가 */

        Member member = memberRepository.findByKakaoId(kakaoId);
        checkMemberStatus(member);    /** 방에 참여하려는 사용자가 혹시 1차 2차 인증 과정을 모두 거치지는 않았는지 */
        checkBelongAnotherRoom(member);/** 혹시 이미 이방 or 다른방에 참여하고 있는 사용자는 아닌가 */
        /** matching.Y 된 방은 못들어가게 validaion , 성별도 같이 확인 */

        //2. kakaoId를 가지고 참여하고자 하는 그 Member를 조회하고
        // roomCode를 가지고 참여할 Room을 조회하여
        // 이들을 가지고 RoomMember를 생성하여 save 하므로써
        // 해당 Member를 해당 Room에 속하게 해줌
        Room room = roomRepository.findByRoomCodeAndStatus(roomCode, Status.ACTIVE);

        belongToRoom(member, room);

        //3. 현재 방에 속한 모든 RoomMember의 정보를 DTO로 치환하여 반환
        return getAllRoomMembers(room);
    }



    private void checkDeleteRoomMember(Member member){
        Member findMember = memberRepository.findWithRoomOptional(member.getKakaoId()).orElseThrow(
                () -> {
                    throw new BaseException(BaseResponseStatus.NOT_BELONG_TO_ROOM, "방 삭제 or 방 나가기 시점 : 방을 삭제하거나, 방에서 나가려는 사용자는 사실 어떤 방에도 속해있지 않으므로 방에서 나가거나 방을 삭제할 수 없습니다.");
                }
        );

        /** 혹은 연관된 RoomMember와 Room이 있다고 하더라도 -> 연관된 RoomMember가 INACTIVE 상태라면 이미 방을 나간 상태이니깐 , 조회 후에도 이를 check해줘야 함 */
        if(findMember.getRoomMember().getStatus()==Status.INACTIVE){
            throw new BaseException(BaseResponseStatus.NOT_BELONG_TO_ROOM, "방 삭제 or 방 나가기 시점 : 방을 삭제하거나, 방에서 나가려는 사용자는 사실 어떤 방에도 속해있지 않으므로 방에서 나가거나 방을 삭제할 수 없습니다.");
        }
    }




    private void checkAlreadyGoOut(Member member){
        /** ACTIVE하게 대응되는 MemberRoom이 없다는건 - 이미 삭제되었다는 뜻  */
        roomMemberRepository.findByMemberIdxAndStatus(member.getIdx(), Status.ACTIVE).orElseThrow(
                () -> {throw new BaseException(BaseResponseStatus.DELETED_ROOM_MEMBER, "방 삭제 or 방 나가기 시점 : 방에서 나가려는 or 방 자체를 삭제하려는 해당 사용자는 이미 방에서 나간 상태입니다.");}

        );
    }

    private List<MemberDto>  deleteRoomMemberAtOwner(Member member){
        member.getRoom().getRoomMemberList().stream()
                .forEach(rm -> roomMemberRepository.deleteByMemberIdx(rm.getMember().getIdx()));

        roomRepository.deleteByMemberIdx(member.getIdx());

        return new ArrayList<>();

    }

    private List<MemberDto>  deleteRoomMemberAtNotOwner(Member member){
        roomMemberRepository.deleteByMemberIdx(member.getIdx());
        return  getAllRoomMembers(member.getRoomMember().getRoom());

    }

    @Transactional
    public List<MemberDto> deleteRoomMember(Long kakaoId) {
        //1. 방을 나가려는 그 Member를 조회하여
        Member member = memberRepository.findByKakaoId(kakaoId);

        //2. 유효성 검사 : 이 사람이 속한 방이 있는지
        checkMemberStatus(member);    /** 방에서 나가려는 사용자가 혹시 1차 2차 인증 과정을 모두 거치지는 않았는지 */
        checkAlreadyGoOut(member);    /** 방에서 나가려는 사용자가 이미 방에서 나갔는지 */

        //3. (위 검증을 모두 겨쳤다면) kakaoId를 통해 Member~RoomMember~Room을 모두 조회
        member = memberRepository.findWithRoom(kakaoId);

        //4_1. 방장이면
        if(member.getRoomMember().getRoom().getOwnerMember().getIdx() == memberRepository.findByKakaoId(kakaoId).getIdx()) {
            return deleteRoomMemberAtOwner(member);
        }

        //4_2. 일반 사용자면
        else{
            return deleteRoomMemberAtNotOwner(member);
        }

    }

}
