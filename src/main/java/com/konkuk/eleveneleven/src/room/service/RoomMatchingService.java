package com.konkuk.eleveneleven.src.room.service;

import com.konkuk.eleveneleven.common.enums.Gender;
import com.konkuk.eleveneleven.common.enums.Status;
import com.konkuk.eleveneleven.src.room.Room;
import com.konkuk.eleveneleven.src.room.repository.RoomRepository;
import com.konkuk.eleveneleven.src.room.vo.RandMatchResult;
import com.konkuk.eleveneleven.src.room_member.RoomMember;
import com.konkuk.eleveneleven.src.room_member.repository.RoomMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@EnableScheduling
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomMatchingService {

    /**
     * [ 매칭 구조 ]
     *  Room Entity 를 기준으로 매일 23시에 Scheduler를 동작하여 Gender가  MALE, FEMALE 을 각각 모두 뽑아내어 이들을 무작위 매칭
     *
     * [ 무작위 매칭 방식 ]
     *  1. MALE , FEMALE 을 각각 List로 뽑아냄
     *  2. 두 배열중 size가 큰 배열을 기준으로 Random 함수를 사용하여 요소를 하나씩 랜덤 추출
     *  3. 추출한 요소를 size가 작은 배열에 순서대로 매칭
     *
     * [ 방 매칭 진행 방식 ]
     *  1. RoomMember에 FEMALE Room_idx를 변경된 Room으로 바꿔줌
     *  2. MALE Room 만 남기고 FEMALE Room은 삭제(INACTIVE or DELETE)
     *
     * [ 매칭 후 매칭 안된 방 삭제 방식 ]
     *  INACTIVE or DELETE
     *
     * [ FireBase에 정보 전달 ]
     *  매칭 성공 정보와 매칭 실패 정보를 FireBase로 전달
     * */

    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;

    /** 매일 23시에 Scheduler를 동작하여 matching 진행 */
    @Transactional
    @Scheduled(cron = "0 0 23 * * ?")
    public void matchRoom(){

        randMatchRoom();

    }

    /** [ 방 무작위 매칭 ] */
    private RandMatchResult randMatchRoom(){
        // 1. MALE , FEMALE 을 각각 List로 뽑아냄
        List<Room> maleRoomList = roomRepository.findByGenderAndStatus(Gender.MALE, Status.ACTIVE);
        List<Room> femaleRoomList = roomRepository.findByGenderAndStatus(Gender.FEMALE, Status.ACTIVE);

        // 1_except1
        if (maleRoomList.isEmpty() || femaleRoomList.isEmpty()){
            // log에 ERROR 띄움 && FireBase에 Error 전달
        }

        /**  이 친구 나누고 싶다 */
        // 2. 두 배열중 size가 큰 배열을 기준으로 Random 함수를 사용하여 요소를 하나씩 랜덤 추출
        Map<Gender,List<Room>> failMatchRoomMap = new HashMap<>(); // 매칭에 실패한 <성별 , RoomList> Map

        if (maleRoomList.size() >= femaleRoomList.size()){
            Collections.shuffle(maleRoomList);
            failMatchRoomMap.put(Gender.MALE,maleRoomList.subList(femaleRoomList.size(), maleRoomList.size()));

            maleRoomList = maleRoomList.subList(0, femaleRoomList.size())
            updateRoomInfo(maleRoomList, femaleRoomList);
        } else {
            Collections.shuffle(femaleRoomList);
            failMatchRoomMap.put(Gender.FEMALE,femaleRoomList.subList(maleRoomList.size(), femaleRoomList.size()));

            femaleRoomList = femaleRoomList.subList(0, maleRoomList.size())
            updateRoomInfo(maleRoomList,femaleRoomList);
        }

        return RandMatchResult.builder()
                .failMatchRoomMap(failMatchRoomMap)
                .successMatchRoomMale(maleRoomList)
                .successMatchRoomFemale(femaleRoomList)
                .build();
    }

    /** [ 리스트 요소를 랜덤 추출 ] */
    private List<Room> randSortList(List<Room> unsortedList, int criteriaSize){
        Collections.shuffle(unsortedList);
        unsortedList.subList(0,criteriaSize);

        return unsortedList;
    }

    /** [ 방 매칭 진행 ] */
    public void updateRoomInfo(List<Room> maleRoomList, List<Room> femaleRoomList){
        // 1. RoomMember에 FEMALE Room을 변경된 MALE Room으로 바꿔줌
        IntStream.range(0, femaleRoomList.size())
                .forEach(i -> updateRoomMember(femaleRoomList.get(i).getIdx(), maleRoomList.get(i)) );

        // 2. 매칭된 FEMALE ROOM 삭제
        deleteNotMatchRoom(femaleRoomList);
    }

    /** [ FEMALE RoomMember의 Room을 남자 Room으로 변경 ] */
    public void updateRoomMember(Long femaleRoomIdx, Room maleRoom){
        RoomMember roomMember = roomMemberRepository.findByIdx(femaleRoomIdx);
        roomMember.updateRoom(maleRoom);
    }

    /** [ 매칭 안된 방들 삭제 ] */
    public void deleteNotMatchRoom(List<Room> deleteRoomList){

//        roomMemberRepository.deleteAllById(deleteRoomList.stream()
//                .map(drl -> drl.getRoomMemberList())
//                .map(rml -> rml.stream()
//                        .map(rm -> rm.getIdx())
//                        .collect(Collectors.toList()));

        roomRepository.deleteAllById(deleteRoomList.stream()
                .map(drl -> drl.getIdx())
                .collect(Collectors.toList()));
    }


}
