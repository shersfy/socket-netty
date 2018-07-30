package org.shersfy.server.test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig.Builder;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketListener;
import org.asynchttpclient.ws.WebSocketTextListener;
import org.asynchttpclient.ws.WebSocketUpgradeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Socket 客户端
 * @author py
 * 2018年7月5日
 */
public class WebsocketClient implements WebSocketTextListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebsocketClient.class);
	private static final long CLIENT_RECONNECT_TIME    = 1 * 60 * 1000;

	private WebSocket websocket;
	private AtomicInteger continuousFailTimes = new AtomicInteger(0);
	private String url;

	public WebsocketClient(String url){
	    this.url = url;
	}

	public void connect() {
		int times = continuousFailTimes.get();
		if (times > 0) {
			try {
				long sleepTime = ((long) Math.pow(2, times)) * 5000;
				if(sleepTime > CLIENT_RECONNECT_TIME){
					sleepTime = CLIENT_RECONNECT_TIME;
					continuousFailTimes.set(0);
				}
				LOGGER.info("{}s later, reconnect again", (sleepTime/1000));
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				LOGGER.error("InterruptedException", e);
			}
		}
		
		try {
			
			websocket = requestWebsockConnection(this);
			LOGGER.info("client connected, status: online");
			LOGGER.debug("WebsockUrl={}", url);
			
		} catch (InterruptedException | ExecutionException e) {
			LOGGER.error("InterruptedException | ExecutionException", e);
			continuousFailTimes.incrementAndGet();
			connect();
		} catch (Exception ex) {
			LOGGER.error("", ex);
		}
	}

	@Override
	public void onOpen(WebSocket websocket) {
		// 成功了会调用onPen，重置连续失败次数
		continuousFailTimes.set(0);
		websocket.sendMessage("config");
	}

	@Override
	public void onMessage(String message) {
		LOGGER.info("received message from server. msg={}", message);
	}

	@Override
	public void onClose(WebSocket websocket) {
		LOGGER.info("Websocket to {} closed", websocket.getRemoteAddress());
		continuousFailTimes.incrementAndGet();
		connect();
	}

	@Override
	public void onError(Throwable t) {
		LOGGER.info("onError", t);
	}

	public WebSocket getWebsocket() {
		return websocket;
	}
	
	public WebSocket requestWebsockConnection(WebSocketListener listener) 
        throws InterruptedException, ExecutionException{
    
    WebSocket websocket = null;
    StringBuffer params = new StringBuffer("");
    
    params.append("?token=").append("accel-ppp");
    
    Builder config = new Builder();
    // 接受任何证书
    config.setAcceptAnyCertificate(true);
    
    AsyncHttpClient atc = Dsl.asyncHttpClient(config);
    WebSocketUpgradeHandler builder = new WebSocketUpgradeHandler.Builder().addWebSocketListener(listener).build();
    websocket = atc.prepareGet(url + params.toString()).execute(builder).get();
    
    return websocket;
}

}
