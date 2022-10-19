package com.konkuk.eleveneleven.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@EnableWebSocketMessageBroker // 스프링에서 제공하는 내장 메세지 브로커를 사용하겠다
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // 해당 값이 prefix로 붙은 메세지가 송신되었을 떄 ->  메세지를 내장  메세지 브로커가 처리하겠다는 설정
    /** *
     * [참고사항]
     * /queue 라는 prefix는 메세지가 1:1로 송신될 떄
     * /topic 이라는 prefix는 메세지가 1:다로 송신될 때 주로 사용한다고 함
     */



    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        /** 해당 값이 prefix로 붙은 메세지가 송신되었을 때 , 그 메세지를 메세지 브로커가 처리하겠다
         * 여기서는 /queue , /topic으로 시작하는 url로 메세지가 전송되면 -> 메세지 브로커가 그 메세지를 받고 , 구독자들에게 전달해줌 */
        config.enableSimpleBroker("/sub", "/topic"); // 바로 메세지 브로커로 메세지를 봅내는 경우

        /** setApplicationDestinatinoPrefixes() 는 메세지를 가공하는 핸들러로 메세지가 라우팅 되도록 하는 설정 */
        config.setApplicationDestinationPrefixes("/pub"); // 가공 등을 위해 메세지 핸들러로 보냄 - 거기서 가공 후 - 메세지 브로커로 보냄냄    }
    }

    //처음 웹소켓 핸드쉐이크를 위한 주소
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        registry.addEndpoint("/ws").setAllowedOrigins("*")
                .withSockJS();
    }

}


