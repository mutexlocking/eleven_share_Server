package com.konkuk.eleveneleven.src.room.service;

import com.konkuk.eleveneleven.common.enums.Gender;
import com.konkuk.eleveneleven.common.enums.Status;
import com.konkuk.eleveneleven.src.room.Room;
import com.konkuk.eleveneleven.src.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@EnableScheduling
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomMatchingService {

    /**
     * [ 매칭 구조 ]
     *  Room Entity 를 기준으로 매일 23시에 Scheduler를 동작하여 Gender가  MALE, FEMALE 을 각각 모두 뽑아내어 이들을 무작위 매칭
     * [ 무작위 매칭 방식 ]
     *  1. MALE , FEMALE 을 각각 List로 뽑아냄
     *  2. 두 배열중 size가 큰 배열을 기준으로 Random 함수를 사용하여 요소를 하나씩 랜덤 추출
     *  3. 추출한 요소를 size가 작은 배열에 순서대로 매칭
     * [ 매칭 후 매칭된 방 저장 방식 ]
     *  1. MALE Room 만 남기고 FEMALE Room은 삭제(INACTIVE or DELETE)
     *  2. RoomMember에 FEMALE Room_idx를 변경된 Room으로 바꿔줌
     * [ 매칭 후 매칭 안된 방 삭제 방식 ]
     *  INACTIVE or DELETE
     * */

    private final RoomRepository roomRepository;

    /** 매일 23시에 Scheduler를 동작하여 matching 진행 */
    @Transactional
    @Scheduled(cron = "0 0 23 * * ?")
    public void matchRoom(){

    }

    /** 방 무작위 매칭 */
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
            randSortList(maleRoomList,femaleRoomList.size());
        } else {
            randSortList(femaleRoomList,maleRoomList.size());
        }

    }

    /** 리스트 요소를 랜덤 추출 */
    private List<?> randSortList(List<?> unsortedList, int criteriaSize){
        Collections.shuffle(unsortedList);
        unsortedList.subList(0,criteriaSize);

        return unsortedList;
    }

    /** RoomMember에 FEMALE Room_idx를 변경된 Room으로 바꿈 */
    public void updateRoomIdx(){

    }

    /** 매칭 안된 방들 삭제 */
    public void deleteNotMatchRoom(){

    }


}
