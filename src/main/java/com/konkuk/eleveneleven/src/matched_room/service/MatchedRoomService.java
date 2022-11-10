package com.konkuk.eleveneleven.src.matched_room.service;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.konkuk.eleveneleven.common.enums.Status;
import com.konkuk.eleveneleven.config.BaseException;
import com.konkuk.eleveneleven.config.BaseResponseStatus;
import com.konkuk.eleveneleven.src.matched_room.MatchedRoom;
import com.konkuk.eleveneleven.src.matched_room.dto.UrlDto;
import com.konkuk.eleveneleven.src.matched_room.repository.MatchedRoomRepository;
import com.konkuk.eleveneleven.src.matched_room_member.MatchedRoomMember;
import com.konkuk.eleveneleven.src.matched_room_member.repository.MatchedRoomMemberRepository;
import com.konkuk.eleveneleven.src.member.Member;
import com.konkuk.eleveneleven.src.member.repository.MemberRepository;
import com.konkuk.eleveneleven.src.room.Room;
import com.konkuk.eleveneleven.src.room.repository.RoomRepository;
import com.konkuk.eleveneleven.src.room_member.RoomMember;
import com.konkuk.eleveneleven.src.room_member.repository.RoomMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
//@Transactional(readOnly = true)
public class MatchedRoomService {


    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final MemberRepository memberRepository;
    private final MatchedRoomRepository matchedRoomRepository;
    private final MatchedRoomMemberRepository matchedRoomMemberRepository;




    /** [오픈채팅 url setting 서비스] */
    @Transactional
    public UrlDto insertUrl(Long kakaoId, Long matchedRoomIdx, String url){

        //1_1. 해당 matchedRoomIdx의 PK로써의 유효성을 검사하면서 && 동시에 그 matchedRoom의 Owner가 이 kakaoId를 가진 Member임을 검사
        MatchedRoom matchedRoom = getMatchedRoom(matchedRoomIdx);
        checkOwner(matchedRoom, kakaoId);

        //1_2. 또한 아직 그 MatchedRoom의 url이 설정되지 않았음을 검사
        checkUrlAlready(matchedRoom);

        checkUrlPattern(url);

        //2. 넘어온 url로 setting
        matchedRoom.setUrl(url);

        //3. urlDto 생성하여 반환
        return UrlDto.builder()
                .url(url)
                .matchedRoomIdx(matchedRoom.getIdx())
                .build();

    }

    /** [Room의 정보들을 Matched_Room로 이전] */
    public void migrateRoomToMatchedRoom(){
        List<Room> allRoomInDB = roomRepository.findAll();

        for (Room room : allRoomInDB) {
            MatchedRoom matchedRoom = new MatchedRoom(room.getOwnerMember());
            matchedRoomRepository.save(matchedRoom);

            migrateRoomMemberToMatchedRoomMember(room.getIdx(),matchedRoom);
        }
    }

    /** [Room_Member의 정보들을 Matched_Room_Member로 이전] */
    private void migrateRoomMemberToMatchedRoomMember(Long roomIdx, MatchedRoom matchedRoom){
        List<RoomMember> allRoomMemberInRoom = roomMemberRepository.findByRoomIdx(roomIdx);

        for (RoomMember roomMember : allRoomMemberInRoom){
            matchedRoomMemberRepository.save(new MatchedRoomMember(matchedRoom,roomMember.getMember()));
        }
    }

    private MatchedRoom getMatchedRoom(Long matchedRoomIdx){
        if(matchedRoomRepository.existsById(matchedRoomIdx)){
            return matchedRoomRepository.findMatchedRoomFetch(matchedRoomIdx).get();
        } else{
            throw new BaseException(BaseResponseStatus.INVALID_MATCHED_ROOM_IDX, "오픈채팅 url 설정 시점 : 설정하려고 하는 MatchedRoom의 Idx값이 유효하지 않습니다.");
        }
    }

    private void checkOwner(MatchedRoom matchedRoom, Long kakaoId){
        if(matchedRoom.getOwnerMember().getKakaoId()!=kakaoId){
            throw new BaseException(BaseResponseStatus.IS_NOT_MATCHED_ROOM_OWNER, "오픈채팅 url 설정 시점 : url을 설정하려고 하는 사용자가 , 그 MatchedRoom의 방장이 아닙니다.");
        }
    }

    private void checkUrlPattern(String url){

        if( !url.contains("open.kakao.com")){
            throw new BaseException(BaseResponseStatus.IS_NOT_OPEN_URL, "오픈채팅 url 공유 시점 : 오픈채팅 url이 아님");
        }
    }

    private void checkUrlAlready(MatchedRoom matchedRoom){
        if(Optional.ofNullable(matchedRoom.getUrl()).isPresent()){
            throw new BaseException(BaseResponseStatus.ALREADY_SETTING_URL, "오픈채팅 url 공유 시점 : 오픈채팅 url이 이미 공유되어 있습니다.");
        }
    }

    /** [오픈채팅 url 조회 서비스] */
    public UrlDto getUrl(Long kakaoId, Long matchedRoomIdx){

        //1_1. matchedRoomIdx의 유효성을 검사한 후
        MatchedRoom matchedRoom = getMatchedRoom(matchedRoomIdx);

        //1_2. 해당 사용자가 , 그 MatchedRoom에 소속된 Member인지를 검증
        checkMatchedRoomMember(matchedRoom,kakaoId);

        //1_3. 또한 그 MatchedRoom에 url이 setting되어 있는지 검증
        checkUrl(matchedRoom);


        //2. 설정되어있는 url을 반환
        return UrlDto.builder()
                .matchedRoomIdx(matchedRoom.getIdx())
                .url(matchedRoom.getUrl())
                .build();
    }

    private void checkMatchedRoomMember(MatchedRoom matchedRoom, Long kakaoId){

        Member member = memberRepository.findByKakaoId(kakaoId);

        if( !matchedRoomMemberRepository.existsByMemberIdxAndMatchedRoomIdxAndStatus(member.getIdx(), matchedRoom.getIdx(), Status.ACTIVE)){
            throw new BaseException(BaseResponseStatus.IS_NOT_BELEONG_TO_MATCHED_ROOM, "오픈채팅 url 조회 시점 : 해당 사용자는 , 해당 MatchedRoom에 속한 사용자가 아니므로 , url을 공유받을 수 없습니다.");
        }
    }

    private void checkUrl(MatchedRoom matchedRoom){
        Optional.ofNullable(matchedRoom.getUrl()).orElseThrow(
                () -> {throw new BaseException(BaseResponseStatus.IS_NOT_SETTING_URL, "오픈채팅 url 조회 시점 : 아직 해당 MatchedRoom에는 url이 설정되어 있지 않습니다.");}
        );
    }


}
