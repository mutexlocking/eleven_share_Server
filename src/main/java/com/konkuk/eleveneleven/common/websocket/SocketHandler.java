package com.konkuk.eleveneleven.common.websocket;

import com.konkuk.eleveneleven.config.BaseException;
import com.konkuk.eleveneleven.config.BaseResponseStatus;
import com.konkuk.eleveneleven.src.room.dto.MemberDto;
import com.konkuk.eleveneleven.src.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SocketHandler extends TextWebSocketHandler {


    private final Map<Long, List<SessionPair>> sessionMap = new ConcurrentHashMap<>();
    private final RoomService roomService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        Object payloadObj = message.getPayload();
        String payload = payloadObj.toString();
        JSONObject payloadJson = new JSONObject(payload);

        String role = payloadJson.getString("role");
        long roomIdx = payloadJson.getLong("roomIdx");
        boolean isOwner = payloadJson.getBoolean("isOwner");
        List<MemberDto> memberDtoList = roomService.getMembers(roomIdx);

        if(role.equals("enter")){

            if(isOwner){

                //새로운 keyPair를 만들고
                List<SessionPair> sessionPairList = new ArrayList<>();
                sessionPairList.add(SessionPair.builder().sessionId(session.getId()).session(session).build());
                sessionMap.put(roomIdx, sessionPairList);


                //현재 참여한 인원의 정보를 보낸다 - sendAll
                sendAll(sessionMap, roomIdx, memberDtoList);

            } else{
                //keyList에서 roomIdx로 대응되는 keyPair를 찾고
                List<SessionPair> sessionList = sessionMap.get(roomIdx);

                //그 keyPair의 sessionList에 현재 session을 추가해준다
                sessionList.add(SessionPair.builder().sessionId(session.getId()).session(session).build());

                //그 keyPair에 대해 현재 참여한 인원의 정보를 보낸다. - sendAll
                sendAll(sessionMap, roomIdx, memberDtoList);


            }

        }
        else if(role.equals("exit")){

            if(isOwner){
                //sessionMap에서 그 keyPair 자체를 제거한다
                sessionMap.remove(roomIdx);

                //그 후엔 알림을 보내든 뭘 하든 ~
            }
            else{
                //kekyLiST에서 그 keyPair를 찾고
                List<SessionPair> sessionList = sessionMap.get(roomIdx);

                //그 keyPair의 SessionList에서 특정 session을 제거 후
                sessionList.removeIf(sp -> sp.getSessionId().equals(session.getId()));
                System.out.println("memberDtoList = " + memberDtoList.size());

                //나머지 세션들에게 sendAll
                sendAll(sessionMap, roomIdx, memberDtoList);
            }
        }


    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
    }

    private void sendAll(Map<Long, List<SessionPair>> sessionMap, Long roomIdx, List<MemberDto> memberDtoList){
        sessionMap.get(roomIdx).stream()
                .map(sp -> sp.getSession())
                .forEach(s -> {
                    try {
                        s.sendMessage(new TextMessage(memberDtoList.toString()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}
