package org.shersfy.server.websocket;

import java.io.IOException;

import org.shersfy.server.beans.MessageData;
import org.shersfy.server.websocket.RequestHandlerExternal.RequestCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import com.alibaba.fastjson.JSON;

/**
 * WebSocketHandler处理装饰扩展
 * @author py
 * 2018年7月6日
 */
public class WebSocketHandlerDecoratorExp extends WebSocketHandlerDecorator {
    
    protected static final Logger LOGGER = LoggerFactory.getLogger(WebSocketHandlerDecoratorExp.class);
    
    private RequestHandlerExternal requestHandler;

    public WebSocketHandlerDecoratorExp(WebSocketHandler delegate, RequestHandlerExternal requestHandler) {
        super(delegate);
        this.requestHandler = requestHandler;
    }
    
    @Override
    public boolean supportsPartialMessages() {
        return super.supportsPartialMessages();
    }
    
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception)
        throws Exception {
        super.handleTransportError(session, exception);
        
    }
    
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message)
        throws Exception {
        LOGGER.debug("{}:{}, msg: {}", 
            session.getRemoteAddress().getHostName(),
            session.getRemoteAddress().getPort(),
            message.getPayload());
        
        // 处理message
        try {
            MessageData msg = JSON.parseObject(String.valueOf(message.getPayload()), MessageData.class);
            requestHandler.handleRequest(new RequestCallback() {}, msg);
        } catch (Exception e) {
            LOGGER.info("{}:{}, msg: {}", 
                session.getRemoteAddress().getHostName(),
                session.getRemoteAddress().getPort(),
                message.getPayload());
        }
    }
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        LOGGER.info("====================");
        LOGGER.info("session(id={}), {}:{} connected", 
            session.getId(),
            session.getRemoteAddress().getHostName(),
            session.getRemoteAddress().getPort());
        LOGGER.info("====================");
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus)
        throws Exception {
        super.afterConnectionClosed(session, closeStatus);
        LOGGER.info("====================");
        LOGGER.info("session(id={}), {}:{} closed", 
            session.getId(),
            session.getRemoteAddress().getHostName(),
            session.getRemoteAddress().getPort());
        LOGGER.info("====================");
    }
    
    public void sendMsg(WebSocketSession session, MessageData msg) throws IOException {
        TextMessage text = new TextMessage(msg.toString());
        session.sendMessage(text);
    }

}
