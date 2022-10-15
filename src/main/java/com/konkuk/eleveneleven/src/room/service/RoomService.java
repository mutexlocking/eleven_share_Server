package com.konkuk.eleveneleven.src.room.service;

import com.konkuk.eleveneleven.src.chat.vo.ChatRoom;
import com.konkuk.eleveneleven.src.room.repository.RoomRepository;
import com.konkuk.eleveneleven.src.room.vo.WaitingRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RoomService {

    private Map<String, WaitingRoom> waitingRoomMap;

    /** DI 후 초기화 LinkedHashMap<> 초기화 */
    @PostConstruct
    private void init(){
        waitingRoomMap = new LinkedHashMap<>();
    }

    public WaitingRoom findRoomById(String id){
        return waitingRoomMap.get(id);
    }

    public WaitingRoom createWaitingRoom(String name){
        WaitingRoom room = WaitingRoom.create(name);
        waitingRoomMap.put(room.getRoomId(), room);

        return room;
    }

    public List<WaitingRoom> findAllRooms(){
        //채팅방 생성 순서 최근 순으로 반환
        List<WaitingRoom> result = new ArrayList<>(waitingRoomMap.values());
        Collections.reverse(result);

        return result;
    }

}
