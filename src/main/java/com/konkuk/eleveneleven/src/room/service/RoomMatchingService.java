package com.konkuk.eleveneleven.src.room.service;

import com.konkuk.eleveneleven.common.enums.Gender;
import com.konkuk.eleveneleven.common.enums.Status;
import com.konkuk.eleveneleven.src.room.Room;
import com.konkuk.eleveneleven.src.room.repository.RoomRepository;
import com.konkuk.eleveneleven.src.room_member.RoomMember;
import com.konkuk.eleveneleven.src.room_member.repository.RoomMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
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
     * [ 매칭 후 매칭된 방 저장 방식 ]
     *  1. RoomMember에 FEMALE Room_idx를 변경된 Room으로 바꿔줌
     *  2. MALE Room 만 남기고 FEMALE Room은 삭제(INACTIVE or DELETE)
     *
     * [ 매칭 후 매칭 안된 방 삭제 방식 ]
     *  INACTIVE or DELETE
     * */

    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;

    /** 매일 23시에 Scheduler를 동작하여 matching 진행 */
    @Transactional
    @Scheduled(cron = "0 0 23 * * ?")
    public void matchRoom(){

    }

    /** [ 방 무작위 매칭 ] */
    private void randMatchRoom(){
        // 1. MALE , FEMALE 을 각각 List로 뽑아냄
        List<Room> maleRoomList = roomRepository.findByGenderAndStatus(Gender.MALE, Status.ACTIVE);
        List<Room> femaleRoomList = roomRepository.findByGenderAndStatus(Gender.FEMALE, Status.ACTIVE);

        // 1_except1
        if (maleRoomList.isEmpty() || femaleRoomList.isEmpty()){
            // log에 ERROR 띄움 && FireBase에 Error 전달
        }

        // 2. 두 배열중 size가 큰 배열을 기준으로 Random 함수를 사용하여 요소를 하나씩 랜덤 추출
        if (maleRoomList.size() >= femaleRoomList.size()){
            maleRoomList = randSortList(maleRoomList,femaleRoomList.size());
        } else {
            femaleRoomList = randSortList(femaleRoomList,maleRoomList.size());
        }



    }

    /** [ 리스트 요소를 랜덤 추출 ] */
    private List<Room> randSortList(List<Room> unsortedList, int criteriaSize){
        Collections.shuffle(unsortedList);
        unsortedList.subList(0,criteriaSize);

        // 매칭 안된 

        return unsortedList;
    }

    /** [ 매칭 후 매칭된 방 저장 ] */
    public void updateRoomInfo(List<Room> maleRoomList, List<Room> femaleRoomList){
        // 1. RoomMember에 FEMALE Room을 변경된 MALE Room으로 바꿔줌
        IntStream.range(0, femaleRoomList.size())
                .forEach(i -> updateRoomMember(femaleRoomList.get(i).getIdx(), maleRoomList.get(i)) );

        // 2. 매칭된 FEMALE ROOM 삭제
        deleteNotMatchRoom(femaleRoomList);
    }

    /** FEMALE RoomMember의 Room을 남자 Room으로 변경 */
    public void updateRoomMember(Long femaleRoomIdx, Room maleRoom){
        RoomMember roomMember = roomMemberRepository.findByIdx(femaleRoomIdx);
        roomMember.updateRoom(maleRoom);
    }

    /** [ 매칭 안된 방들 삭제 ] */
    public void deleteNotMatchRoom(List<Room> deleteRoomList){
        roomRepository.deleteAllById(deleteRoomList.stream()
                .map(drl -> drl.getIdx())
                .collect(Collectors.toList()));
    }


}
