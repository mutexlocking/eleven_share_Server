package com.konkuk.eleveneleven.common.websocket;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class WebSocketRequest {

    private Boolean isOwner;
    private Long roomIdx;
}
