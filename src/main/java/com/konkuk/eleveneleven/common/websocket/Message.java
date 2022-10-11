package com.konkuk.eleveneleven.common.websocket;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Message {
    private String type;
    private String sender;
    private String receiver;
    private Object data;

    public void newConnect(){
        this.type = "new";
    }

    public void closeConnect(){
        this.type = "close";
    }
}
