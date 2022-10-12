package com.konkuk.eleveneleven.common.websocket;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KeyPair {

    private Long roomIdx;
    private List<SessionPair> sessionList = new ArrayList<>();
}
