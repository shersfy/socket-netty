package org.shersfy.server.config;


import javax.annotation.Resource;

import org.shersfy.server.websocket.RequestHandlerExternal;
import org.shersfy.server.websocket.WebSocketHandlerDecoratorExp;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

/**
 * WebSocket+STOMP配置, 支持SockJS及普通websocket客户端通信
 * @author py
 * 2018年7月6日
 */
//@Configuration
//@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Resource
    private RequestHandlerExternal requestHandlerExternal;
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册一个名字为"ws" 的endpoint接收客户端的连接
        registry.addEndpoint("/ws");
        registry.addEndpoint("/wsjs").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 规范: 客户端订阅地址的前缀信息
        registry.enableSimpleBroker("/topic");
        // 规范: 客户端给服务端发消息的地址的前缀
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.addDecoratorFactory(new WebSocketHandlerDecoratorFactory() {
            
            @Override
            public WebSocketHandler decorate(WebSocketHandler handler) {
                return new WebSocketHandlerDecoratorExp(handler, requestHandlerExternal);
            }
        });
    }
    
    

}
