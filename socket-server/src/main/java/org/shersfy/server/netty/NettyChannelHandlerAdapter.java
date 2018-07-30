package org.shersfy.server.netty;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.shersfy.server.beans.MessageData;
import org.shersfy.server.websocket.RequestHandlerExternal;
import org.shersfy.server.websocket.RequestHandlerExternal.RequestCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.channel.ChannelHandler.Sharable;

@Component
@Sharable
public class NettyChannelHandlerAdapter extends ChannelInboundHandlerAdapter{

    protected static final Logger LOGGER = LoggerFactory.getLogger(NettyChannelHandlerAdapter.class);
   
    @Resource
    private RequestHandlerExternal requestHandler;
   
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("channel {} connected", ctx.channel());
        super.handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("channel {} disconnected", ctx.channel());
        super.handlerRemoved(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        String error = cause==null?"":cause.getMessage();
        if(cause instanceof CorruptedFrameException && error!=null && error.contains("position")) {
            String substr = StringUtils.substringAfterLast(error, ":").trim();
            error = StringUtils.replace(error, substr, new String(ByteBufUtil.decodeHexDump(substr)));

        }
        LOGGER.error("channel {} error: {}", ctx.channel(), error);
        ctx.close();
    }

    /**
     * data = data1+data2+data3
     * client.out.flush(data)
     * 客户端flush后，一个data切分(decode)调用一次, 如上调用3此
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(!(msg instanceof ByteBuf)) {
            LOGGER.warn("channel {} unknown message type: {}", ctx.channel(), msg);
            return;
        }
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        
        String text = new String(bytes, "UTF-8");
        LOGGER.info("channel {} msg: {}", ctx.channel(), text);
        try {
            MessageData data = JSON.parseObject(text, MessageData.class);
            // 异步处理业务
            requestHandler.handleRequest(new RequestCallback() {}, data);
        } catch (Exception e) {
            LOGGER.info("channel {} msg: {}", ctx.channel(), text);
        }
    }

    /**
     * data = data1+data2+data3
     * client.out.flush(data)
     * 客户端一次flush，调用一次该方法
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }
    
    

}
