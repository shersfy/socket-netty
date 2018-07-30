package org.shersfy.server.test;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringEncoder;

public class SocketServer {
    private final int port;

    public SocketServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap sb = new ServerBootstrap();
            sb.option(ChannelOption.SO_BACKLOG, 1024);
            sb.group(group, bossGroup) // 绑定线程池
            .channel(NioServerSocketChannel.class) // 指定使用的channel
            .localAddress(this.port)// 绑定监听端口
            .childHandler(new ChannelInitializer<SocketChannel>() { // 绑定客户端连接时候触发操作

                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    System.out.println("initChannel");
                    System.out.println("IP:" + ch.localAddress().getHostName());
                    System.out.println("Port:" + ch.localAddress().getPort());

                    ch.pipeline().addLast(new StringEncoder(Charset.forName("UTF-8")));
                    ch.pipeline().addLast(new ServerHandler()); // 客户端触发操作
                    ch.pipeline().addLast(new ByteArrayEncoder());
                }
            });
            ChannelFuture cf = sb.bind().sync(); // 服务器异步创建绑定
            System.out.println(SocketServer.class + " 启动正在监听： " + cf.channel().localAddress());
            cf.channel().closeFuture().sync(); // 关闭服务器通道
        } finally {
            group.shutdownGracefully().sync(); // 释放线程池资源
            bossGroup.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception {

        new SocketServer(20181).start(); // 启动
    }

    static public class ServerHandler extends ChannelInboundHandlerAdapter {

        /**
         * 当客户端主动链接服务端的链接后，这个通道就是活跃的了。也就是客户端与服务端建立了通信通道并且可以传输数据
         */
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println(ctx.channel().localAddress().toString() + "channelActive通道已激活");
        }

        /**
         * 当客户端主动断开服务端的链接后，这个通道就是不活跃的。也就是说客户端与服务端的关闭了通信通道并且不可以传输数据
         */
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("channelInactive通道不活跃:"+ctx.channel().localAddress().toString());

        }

        /**
         * 此处用来处理收到的数据中含有中文的时  出现乱码的问题
         * @param buf
         * @return
         */
        private String getMessage(ByteBuf buf) {
            byte[] con = new byte[buf.readableBytes()];
            buf.readBytes(con);
            try {
                return new String(con, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * 功能：读取服务器发送过来的信息
         */
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            // 第一种：接收字符串时的处理
            ByteBuf buf = (ByteBuf) msg;
            String rev = getMessage(buf);
            System.out.println("channelRead来自客户端信息：" + rev);

        }

        /**
         * 功能：读取完毕客户端发送过来的数据之后的操作
         */
        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            System.out.println("channelReadComplete服务端接收数据完毕..");
            // 第一种方法：写一个空的buf，并刷新写出区域。完成后关闭sock channel连接。
            // ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            // ctx.flush();
            // ctx.flush();
            // 第二种方法：在client端关闭channel连接，这样的话，会触发两次channelReadComplete方法。
            // ctx.flush().close().sync(); 
            // 第三种：改成这种写法也可以，但是这中写法，没有第一种方法的好。
            super.channelReadComplete(ctx);
        }

        /**
         * 功能：服务端发生异常的操作
         */
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.close();
            System.out.println("exceptionCaught异常信息：\r\n" + cause.getMessage());
        }
    }
}
