package org.shersfy.server.test;

import org.asynchttpclient.ws.WebSocket;

public class WebsocketClientTest {

    public static void main(String[] args) throws InterruptedException {

        WebsocketClient client = new WebsocketClient("ws://localhost:8080/ws");
        client.connect();
        WebSocket socket = client.getWebsocket();
        String msg = "The client sleep %s seconds";
        int cnt = 0;
        while(true) {
            socket.sendMessage(String.format(msg, cnt));
            Thread.sleep(2000);
            cnt +=2;
        }
    }

}
