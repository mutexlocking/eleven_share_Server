package com.konkuk.eleveneleven.common.websocket;

import com.konkuk.eleveneleven.src.room.dto.MemberDto;
import com.konkuk.eleveneleven.src.room.service.RoomService;
import com.konkuk.eleveneleven.src.room_member.RoomMember;
import com.konkuk.eleveneleven.src.room_member.service.RoomMemberService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.sockjs.client.WebSocketClientSockJsSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class SocketHandler extends TextWebSocketHandler {


    Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
    Map<Long, List<String>> keyMap = new ConcurrentHashMap<>();
    private final RoomService roomService;


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessionMap.put(session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        /** (1) 정상적으로 종료된 경우 */
        if(status.equalsCode(CloseStatus.NORMAL)) {
            sessionMap.remove(session.getId());
            return;
        }

        /** (2) 정상적이지 않게 종료된 경우 */

        // 1) 일단 이제는 쓰레기값이 된 세션 값을 제거
        sessionMap.remove(session.getId());

        // 2) 마찬가지로 이제는 쓰레기값이 된 세션 ID 값을 제거
        removeSessionId(session);
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        String role = getRole(message);
        long roomIdx = getRoomIdx(message);
        boolean isOwner = getIsOwner(message);



        if(role.equals("ENTER")){
            List<MemberDto> memberDtoList = roomService.getMembers(roomIdx);
            if(isOwner){
                //전체 방에대한 리스트를 sessionKeyList를 생성한 후 -> 방장에 대한 sessionKey를 add()
                List<String> sessionKeyList = new ArrayList<>();
                sessionKeyList.add(session.getId()); // <roomIdx1, [id1]>
                keyMap.put(roomIdx, sessionKeyList);

                //이후 전체 방에대한 sessionKeyList에 대한 Session에 , 결과 완료된 Member 정보들을 모두 보냄 (브로드캐스팅)
                sendAll(roomIdx, memberDtoList); // id1 -> s1 -> memberDtoList
            }
            else{
                //추가된 Member에 대한 sessionKey를 add()
                keyMap.get(roomIdx).add(session.getId()); // <roomIdx1 , [id1 , id2]>

                //이후 전체 방에대한 sessionKeyList에 대한 Session에 , 결과 완료된 Member 정보들을 모두 보냄 (브로드캐스팅)
                sendAll(roomIdx, memberDtoList); // id1, id2 ->  ->  <id1, s1> <id3, s3>
            }
        } else if(role.equals("EXIT")){
            if(isOwner){
                //(1) 방에대한 전체 sessionId를 이용해서 , 그 방에 저장된 실제 들을 모두 session를 close
                keyMap.get(roomIdx).stream()
                        .forEach(si -> {
                            try {
                                sessionMap.get(si).close(CloseStatus.NORMAL);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });

                //(2) 전체 방에대한 keyMap의 SessionKeyList를 삭제
                keyMap.remove(roomIdx);

            } else{
                List<MemberDto> memberDtoList = roomService.getMembers(roomIdx);

                //(1) 먼저 나간 Member에 대한 sessionId를 remove() (sessionId가 String이니까 == 가 아니라 equals()로 비교 해야함)
                keyMap.get(roomIdx).removeIf(si -> si.equals(session.getId()));

                //(2) 이후 전체 방에대한 sessionKeyList에 대한 Session에 , 결과 완료된 Member 정보들을 모두 보냄 (브로드캐스팅)
                sendAll(roomIdx, memberDtoList);


                //(3) 마지막으로 Member에 대한 sessionKey를 이용해서 -> 그 session을 강제로 close()
                sessionMap.get(session.getId()).close(CloseStatus.NORMAL);
            }
        }

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        /** 메세지가 안보내진 상황을 감지해서 -> 다시 보내라고 직접 그 세션에 응답을 보내줘야 함 */
    }


    private JSONObject getPayloadJson(TextMessage message) throws JSONException {
        Object payloadObj = message.getPayload();
        String payload = payloadObj.toString();
        return new JSONObject(payload);
    }

    private String getRole(TextMessage message) throws JSONException {
        return getPayloadJson(message).getString("role");
    }

    private long getRoomIdx(TextMessage message) throws JSONException {
        return getPayloadJson(message).getLong("roomIdx");
    }

    private boolean getIsOwner(TextMessage message) throws JSONException {
        return getPayloadJson(message).getBoolean("isOwner");
    }

    /** 이 Idx의 Room에 있는 모든 인원에게 , 이 memberDtoList 정보를 모두 보낸다 */
    private void sendAll(Long roomIdx, List<MemberDto> memberDtoList){
        keyMap.get(roomIdx).stream()
                .map(sk -> sessionMap.get(sk))
                .forEach(s -> {
                    try {
                        s.sendMessage(new TextMessage(memberDtoList.toString()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }


    private void removeSessionId(WebSocketSession session){
        Iterator<Long> keys = keyMap.keySet().iterator();
        while(keys.hasNext()){
            Long key = keys.next();

            if(keyMap.get(key).contains(session.getId())){
                /** (1) 그 sessionID 값을 가지고 있는 리스트에서 , 그 sessionId를 제거해주고 */
                keyMap.get(key).removeIf(si -> si.equals(session.getId()));

                /** (2) 만약 그렇게 sessionId를 제거해줬는데 , 그 리스트가 빈 리스트가 됬다면 , keyMap에서 그 element 자체를 없애줘야 함
                 * -> 그래야 클라가 connect 끊김을 감지하고 재요청을 보냈을 때 -> OPEN 에 의해 무리 없이 동작함! */
                if(CollectionUtils.isEmpty(keyMap.get(key))){
                    keyMap.remove(key);
                }
            }
        }
    }
}
