package org.shersfy.server.controller;


import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebSocketController extends BaseController{
    
//    @Resource
    private SimpMessagingTemplate messagingTemplate;
    
    @GetMapping("/socket")
    public String index() {
        return "socket started";
    }
    
    // 接收客户端信息 /app/msg
    @MessageMapping("/msg")
    public void receivedMessage(String msg) {
        LOGGER.info("received msg: {}", msg);
    }
    
    @SendTo("/topic/msg")
    public void sendMessage(String msg) {
        LOGGER.info("send msg: {}", msg);
    }

    // 接收/app/convert/send信息, 转换并转发到/topic/message
    @MessageMapping("/convert/send")
    public String convertAndSend(String msg) {
        messagingTemplate.convertAndSend("/topic/msg", msg);
        return msg;
    }
}
