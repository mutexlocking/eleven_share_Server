//package com.konkuk.eleveneleven.common.websocket;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Component
//@RequiredArgsConstructor
//public class SocketHandler extends TextWebSocketHandler {
//
//    private final Map<String, List<WebSocketSession>> sessions = new ConcurrentHashMap<>();
//    private final Map<Long, String> idMap = new ConcurrentHashMap<>();
//
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        sessions.put(session.getId(), new ArrayList<>().add(session));
//
//        Message message = Message.builder()
//                .sender(session.getId())
//                .receiver("all")
//                .build();
//
//        message.newConnect();
//
//        sessions.values().stream()
//                .forEach(s -> {
//                    if(s.getId()!=session.getId()){
//                        try {
//                            s.sendMessage(new TextMessage(message.toString()));
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//    }
//
//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        super.handleTextMessage(session, message);
//    }
//
//    @Override
//    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
//        super.handleTransportError(session, exception);
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//        super.afterConnectionClosed(session, status);
//    }
//}
