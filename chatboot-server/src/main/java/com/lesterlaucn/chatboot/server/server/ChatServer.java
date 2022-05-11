package com.lesterlaucn.chatboot.server.server;

import com.lesterlaucn.chatboot.cocurrent.FutureTaskScheduler;
import com.lesterlaucn.chatboot.codec.ProtobufDecoder;
import com.lesterlaucn.chatboot.codec.ProtobufEncoder;
import com.lesterlaucn.chatboot.server.distributed.ImWorker;
import com.lesterlaucn.chatboot.server.distributed.WorkerRouter;
import com.lesterlaucn.chatboot.server.serverHandler.ChatRedirectHandler;
import com.lesterlaucn.chatboot.server.serverHandler.LoginRequestHandler;
import com.lesterlaucn.chatboot.server.serverHandler.RemoteNotificationHandler;
import com.lesterlaucn.chatboot.server.serverHandler.ServerExceptionHandler;
import com.lesterlaucn.chatboot.utils.IOUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;

@Data
@Slf4j
@Service("ChatServer")
public class ChatServer {
    // 服务器端口
    @Value("${server.port}")
    private int port;
    // 通过nio方式来接收连接和处理连接
    private EventLoopGroup bg;
    private EventLoopGroup wg;

    // 启动引导器
    private ServerBootstrap b =
            new ServerBootstrap();
    @Autowired
    private LoginRequestHandler loginRequestHandler;

    @Autowired
    private ServerExceptionHandler serverExceptionHandler;

    @Autowired
    private RemoteNotificationHandler remoteNotificationHandler;

    @Autowired
    private ChatRedirectHandler chatRedirectHandler;

    public void run() {
        //连接监听线程组
        bg = new NioEventLoopGroup(1);
        //传输处理线程组
        wg = new NioEventLoopGroup();
        //1 设置reactor 线程
        b.group(bg, wg);
        //2 设置nio类型的channel
        b.channel(NioServerSocketChannel.class);
        //3 设置监听端口
        String ip = IOUtil.getHostAddress();
        b.localAddress(new InetSocketAddress(ip, port));
        //4 设置通道选项
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

        //5 装配流水线
        b.childHandler(new ChannelInitializer<SocketChannel>() {
            //有连接到达时会创建一个channel
            protected void initChannel(SocketChannel ch) throws Exception {
                // 管理pipeline中的Handler
                ch.pipeline().addLast("deCoder", new ProtobufDecoder());
                ch.pipeline().addLast("enCoder", new ProtobufEncoder());
//                ch.pipeline().addLast("heartBeat", new HeartBeatServerHandler());
                // 在流水线中添加handler来处理登录,登录后删除
                ch.pipeline().addLast("login", loginRequestHandler);
                ch.pipeline().addLast("remoteNotificationHandler", remoteNotificationHandler);
                ch.pipeline().addLast("chatRedirect", chatRedirectHandler);
                ch.pipeline().addLast("serverException", serverExceptionHandler);
            }
        });
        // 6 开始绑定server
        // 通过调用sync同步方法阻塞直到绑定成功

        ChannelFuture channelFuture = null;
        boolean isStart = false;
        while (!isStart) {
            try {

                channelFuture = b.bind().sync();
                log.info("疯狂创客圈 CrazyIM 启动, 端口为： " +
                        channelFuture.channel().localAddress());
                isStart = true;
            } catch (Exception e) {
                log.error("发生启动异常", e);
                port++;
                log.info("尝试一个新的端口：" + port);
                b.localAddress(new InetSocketAddress(port));
            }
        }


        ImWorker.getInst().setLocalNode(ip, port);

        FutureTaskScheduler.add(() ->
        {
            /**
             * 启动节点
             */
            ImWorker.getInst().init();

            /**
             * 启动节点的管理
             */
            WorkerRouter.getInst().init();

        });

        //JVM关闭时的钩子函数
        Runtime.getRuntime().addShutdownHook(
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        // 8 优雅关闭EventLoopGroup，
                        // 释放掉所有资源包括创建的线程
                        wg.shutdownGracefully();
                        bg.shutdownGracefully();
                    }
                }));
        try {
            // 7 监听通道关闭事件
            // 应用程序会一直等待，直到channel关闭
            ChannelFuture closeFuture =
                    channelFuture.channel().closeFuture();
            closeFuture.sync();
        } catch (
                Exception e) {
            log.error("发生其他异常", e);
        } finally {
            // 8 优雅关闭EventLoopGroup，
            // 释放掉所有资源包括创建的线程
            wg.shutdownGracefully();
            bg.shutdownGracefully();
        }

    }

}
