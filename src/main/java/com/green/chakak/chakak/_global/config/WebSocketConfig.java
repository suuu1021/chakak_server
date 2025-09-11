package com.green.chakak.chakak._global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        //메시지 브로커가 처리할 주소(Prefix) 목록을
        config.enableSimpleBroker("/sub", "/topic");

        //클라이언트 -> 서버로 메시지를 보낼 때 붙이는 prefix
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //클라이언트가 서버와 WebSocket 핸드셰이크를 하기 위해 연결포인트
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
    }
}
