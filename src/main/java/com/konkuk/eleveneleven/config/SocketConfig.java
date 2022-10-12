package com.konkuk.eleveneleven.config;

import com.konkuk.eleveneleven.common.websocket.SocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;



@Configuration
@RequiredArgsConstructor
@EnableWebSocket
public class SocketConfig implements WebSocketConfigurer{

    private final SocketHandler socketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        // WebSocket 접속을 위한 EndPoint
        // => ws://localhost:3000/ws/connect
        registry.addHandler(socketHandler, "ws/connect").setAllowedOrigins("*");
    }
}

