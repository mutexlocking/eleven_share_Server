package com.konkuk.eleveneleven.common.websocket;

import lombok.*;
import org.springframework.web.socket.WebSocketSession;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionPair {

    private String sessionId;
    private WebSocketSession session;
}
