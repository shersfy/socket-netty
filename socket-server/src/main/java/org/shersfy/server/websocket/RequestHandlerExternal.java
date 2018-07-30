package org.shersfy.server.websocket;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.shersfy.server.beans.MessageData;
import org.shersfy.server.beans.ResultCode;
import org.shersfy.server.utils.HttpUtil.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 请求处理
 * @author py
 * 2018年7月6日
 */
@Component
public class RequestHandlerExternal {
    
    protected static final Logger LOGGER = LoggerFactory.getLogger(RequestHandlerExternal.class);
    
    @Async
    public void handleRequest(RequestCallback callback, MessageData data) {
        HttpResult result = new HttpResult();
        result.setCode(ResultCode.OK);
        Map<String, Object> params = new HashMap<>();
        try {
            params.put("data", data.getData());
            switch (data.getCode()) {
                case 1:
                	// 执行业务
                    break;
                default:
                    LOGGER.info("unknown request code: {}, data: {}", data.getCode(), data.getData());
                    break;
            }
            if(result.getCode()!=ResultCode.OK) {
                callback.onError(result);
            } else {
                callback.onSuccess(result);
            }
        } catch (Exception e) {
            LOGGER.error("", e);
            callback.onError(result);
        }
    }
    
    
    /**
     * 请求处理回调
     * @author py
     * 2018年7月6日
     */
    public interface RequestCallback{
        
        /**
         * 处理成功回调
         */
        public default void onSuccess(HttpResult res) {
            
        };
        /**
         * 处理错误回调
         * @param ex
         */
        public default void onError(HttpResult res) {
            if(res!=null) {
                LOGGER.error("error code={}, url={}, body={}", res.getCode(), res.getUrl(), StringEscapeUtils.escapeJava(res.getBody()));
            }
        };
        
    }
    
}
