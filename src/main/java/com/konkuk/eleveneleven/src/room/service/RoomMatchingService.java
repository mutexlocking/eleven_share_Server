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
@Transactional(readOnly = false)
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
    public void matchRoom() {

        randMatchRoom();

    }

    /** [ 방 무작위 매칭 ] */
    public RandMatchResult randMatchRoom() {
        // 1. MALE , FEMALE 을 각각 List로 뽑아냄
        List<Room> maleRoomList = roomRepository.findByGenderAndStatus(Gender.MALE, Status.ACTIVE);
        List<Room> femaleRoomList = roomRepository.findByGenderAndStatus(Gender.FEMALE, Status.ACTIVE);

        // 1_except1 : MALE , FEMALE 중 하나라도 ACTIVE 한 튜플이 없을 경우
        if (maleRoomList.isEmpty() || femaleRoomList.isEmpty()){
            // log에 ERROR 띄움 && FireBase에 Error 전달
        }

        RandMatchResult randMatchResult = randSortList(maleRoomList, femaleRoomList);
        return randMatchResult;
    }

    /** [ 방 매칭 진행 ] */
    public void updateRoomInfo(List<Room> maleRoomList, List<Room> femaleRoomList){
        // 1. RoomMember에 FEMALE Room을 변경된 MALE Room으로 바꿔줌
        IntStream.range(0, femaleRoomList.size())
                .forEach(i -> roomMemberRepository.updateRoomMemberRoom(maleRoomList.get(i),femaleRoomList.get(i)));

        // 2. 매칭된 FEMALE ROOM 삭제
//        deleteNotMatchRoom(femaleRoomList);
    }

    /** [ FEMALE RoomMember의 Room을 남자 Room으로 변경 ] */
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

    /** [ 리스트 요소를 랜덤 추출 ] */
    private RandMatchResult randSortList(List<Room> maleRoomList , List<Room> femaleRoomList){
        // 두 배열중 size가 큰 배열을 기준으로 Random 함수를 사용하여 요소를 하나씩 랜덤 추출
        List<Long> successMatchRoomList = new ArrayList<>(); // 매칭에 성공한 Room idx List
        List<Long> failMatchRoomList = new ArrayList<>(); // 매칭에 실패한 Room idx List


        // MALE, FEMALE 을 member 수 별로 분류 후 그 안에서 매칭을 진행해야 한다.
        Map<Integer,List<Room>> maleRoomByMemberCountMap = sortByMemberCountRoom(maleRoomList);
        Map<Integer,List<Room>> femaleRoomByMemberCountMap = sortByMemberCountRoom(femaleRoomList);

        // 같은 member수의 MALE , FEMALE Room끼리 랜덤 매칭한다.
        // 매칭 진행은 MALE을 기준으로 같은 member수를 가진 FEMALE이 존재하는지 확인 후 존재한다면 그 안에서 랜덤 매칭을 진행한다.
        for(Integer key : maleRoomByMemberCountMap.keySet()){
            if(femaleRoomByMemberCountMap.containsKey(key)){
                List<Room> maleRoomByMemberCountList = maleRoomByMemberCountMap.get(key);
                List<Room> femaleRoomByMemberCountList = femaleRoomByMemberCountMap.get(key);

                int maleListSize = maleRoomByMemberCountList.size();
                int femaleListSize = femaleRoomByMemberCountList.size();

                if(maleListSize >= femaleListSize) { // =>  MALE 수가 더 많을 때
                    Collections.shuffle(maleRoomByMemberCountList);
                    // 실패 Room idx 저장
                    failMatchRoomList.addAll(maleRoomByMemberCountList
                            .subList(femaleListSize, maleListSize)
                            .stream().map(mrl -> mrl.getIdx()).collect(Collectors.toList()));

                    // 성공 Room idx 저장
                    maleRoomByMemberCountList = maleRoomByMemberCountList.subList(0,femaleListSize);
                    successMatchRoomList.addAll(maleRoomByMemberCountList
                            .stream().map(mrl -> mrl.getIdx()).collect(Collectors.toList()));
                    successMatchRoomList.addAll(femaleRoomByMemberCountList
                            .stream().map(frl -> frl.getIdx()).collect(Collectors.toList()));

                    // DB에 update <- 무조건 MALE 기준으로 FEMALE의 Room 정보를 바꾼다.
                    updateRoomInfo(maleRoomByMemberCountList,femaleRoomByMemberCountList);
                } else { // => FEMALE 수가 더 많을 때
                    Collections.shuffle(femaleRoomByMemberCountList);
                    // 실패 Room idx 저장
                    failMatchRoomList.addAll(femaleRoomByMemberCountList
                            .subList(maleListSize, femaleListSize)
                            .stream().map(frl -> frl.getIdx()).collect(Collectors.toList()));

                    // 성공 Room idx 저장
                    femaleRoomByMemberCountList = femaleRoomByMemberCountList.subList(0,maleListSize);
                    successMatchRoomList.addAll(femaleRoomByMemberCountList
                            .stream().map(frl -> frl.getIdx()).collect(Collectors.toList()));
                    successMatchRoomList.addAll(maleRoomByMemberCountList
                            .stream().map(mrl -> mrl.getIdx()).collect(Collectors.toList()));

                    // DB에 update <- 무조건 MALE 기준으로 FEMALE의 Room 정보를 바꾼다.
                    updateRoomInfo(maleRoomByMemberCountList,femaleRoomByMemberCountList);
                }
            } else {
                // => 같은 member 수의 FEMALE Room이 없는 경우
                failMatchRoomList.addAll(maleRoomByMemberCountMap.get(key)
                        .stream()
                        .map(mrm -> mrm.getIdx())
                        .collect(Collectors.toList()));
            }
        }
        // FEMALE 기준으로 MALE과 member수를 비교하여 같은 member수의 방이 없다면 failMatchRoomList 에만 추가
        for(Integer key : femaleRoomByMemberCountMap.keySet() ){
            if(!maleRoomByMemberCountMap.containsKey(key)){
                // 같은 member 수의 MALE Room이 없는 경우
                failMatchRoomList.addAll(femaleRoomByMemberCountMap.get(key)
                        .stream()
                        .map(frm -> frm.getIdx())
                        .collect(Collectors.toList()));
            }
        }

        return RandMatchResult.builder()
                .failMatchRoomList(failMatchRoomList)
                .successMatchRoomList(successMatchRoomList)
                .build();
    }

    /** Room List를 해당 Room에 속한 Member수 기준으로 HashMap 화 */
    public Map<Integer, List<Room>> sortByMemberCountRoom(List<Room> roomList){

        Map<Integer,List<Room>> roomByMemberCountMap = new HashMap<>();

        for(Room room : roomList){
            int memberCount = room.getRoomMemberList().size();

            if(!roomByMemberCountMap.containsKey(memberCount)){ // 해당 멤버수 key가 없을 때 key 생성
                roomByMemberCountMap.put(memberCount,new ArrayList<>(Arrays.asList(room)));
            } else{
                roomByMemberCountMap.get(memberCount).add(room);
            }
        }

        return roomByMemberCountMap;
    }


}
