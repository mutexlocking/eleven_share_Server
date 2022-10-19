package com.konkuk.eleveneleven.src.room.controller;

import com.konkuk.eleveneleven.src.chat.vo.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Log4j2
@Controller
@RequiredArgsConstructor
public class StompRoomController {

    private final SimpMessagingTemplate template; // 특정 Broker로 메세지를 전달

    // Client가 SEND할 수 있는 경로
    // WebSocketConfig 에서 설정한 applicationDestinationPrefixes와 @MessageMapping 경로가 병합됨
    // "/pub/waiting/enter"
    @MessageMapping(value = "/waiting/enter")
    public void enter(ChatMessage message){
        log.info("ENTER : "+message);
        message.setMessage(message.getSender() + "님이 대기방에 참여하였습니다.");
        template.convertAndSend("/sub/waiting/room/" + message.getRoomId(), message);
    }

    @MessageMapping(value = "/waiting/message")
    public void message(ChatMessage message){
        log.info("MESSAGE : "+message);
        template.convertAndSend("/sub/waiting/room/" + message.getRoomId(), message);
    }
}
