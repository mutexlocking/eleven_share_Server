package com.konkuk.eleveneleven.src.room.vo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class WaitingRoom {
    private static final long serialVersionUID = 6494678977089006639L;

    private String roomId;
    private String name;
    private Set<WebSocketSession> sessions = new HashSet<>();

    public static WaitingRoom create(String name) {
        WaitingRoom waitingRoom = new WaitingRoom();
        waitingRoom.roomId = UUID.randomUUID().toString();
        waitingRoom.name = name;

        return waitingRoom;
    }
}
