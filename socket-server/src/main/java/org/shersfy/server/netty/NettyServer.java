package org.shersfy.server.netty;


import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.shersfy.server.config.NettyConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

@Component
public class NettyServer extends Thread{

    protected static final Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);
    private static final int defaultPort = 20181;

    @Resource
    private NettyConfig config;
    @Resource
    private NettyChannelHandlerAdapter channelHandlerAdapter;

    private EventLoopGroup boss;
    private EventLoopGroup worker;


    @PostConstruct
    public void init() {
        this.start();
    }
    
    @Override
    public void run() {
        this.startup();
    }
    
    public void startup() {
        int port = config.getPort()<1?defaultPort:config.getPort();
        String level = StringUtils.isBlank(config.getLoggerLevel())?"info":config.getLoggerLevel();
        try {
            ServerBootstrap strap = new ServerBootstrap();
            boss   = new NioEventLoopGroup(1);
            worker = new NioEventLoopGroup();
            strap.group(boss, worker);
            strap.option(ChannelOption.SO_BACKLOG, 1024);
            strap.localAddress(port);
            strap.channel(NioServerSocketChannel.class);
            strap.handler(new LoggingHandler(LogLevel.valueOf(level.toUpperCase())));
            //设置事件处理
            strap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel channel) throws Exception {
                    // 添加处理IO处理到pipeline队列最后
                    channel.pipeline().addLast(new JsonObjectDecoder()); // TCP包接收解码器
                    channel.pipeline().addLast(channelHandlerAdapter);   // 收发包处理器
                    // 收发包处理器，业务线程和编解码线程分开 
                    channel.pipeline().addLast(new DefaultEventExecutorGroup(1), channelHandlerAdapter);
                    channel.pipeline().addLast(new ByteArrayEncoder());  // TCP包发送编码器
                }
            });
            ChannelFuture future = strap.bind().sync();
            // 阻塞
            LOGGER.info("NettyServer started");
            future.channel().closeFuture().sync();
            LOGGER.info("NettyServer stoped");
        } catch (Exception e) {
            LOGGER.error("", e);
        } finally {
            shutdown();
        }
    }

    @PreDestroy
    public void shutdown() {
        LOGGER.error("shutdown ...");
        boss.shutdownGracefully();
        worker.shutdownGracefully();
    }

}

