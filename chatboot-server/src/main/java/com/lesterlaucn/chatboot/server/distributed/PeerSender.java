package com.lesterlaucn.chatboot.server.distributed;

import com.lesterlaucn.chatboot.codec.ProtobufDecoder;
import com.lesterlaucn.chatboot.codec.ProtobufEncoder;
import com.lesterlaucn.chatboot.common.bean.Notification;
import com.lesterlaucn.chatboot.common.bean.UserDTO;
import com.lesterlaucn.chatboot.common.bean.msg.ProtoMsg;
import com.lesterlaucn.chatboot.entity.ImNode;
import com.lesterlaucn.chatboot.server.protoBuilder.NotificationMsgBuilder;
import com.lesterlaucn.chatboot.server.serverHandler.ImNodeExceptionHandler;
import com.lesterlaucn.chatboot.server.serverHandler.ImNodeHeartBeatClientHandler;
import com.lesterlaucn.chatboot.utils.JsonUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * create by lesterlaucn
 **/
@Slf4j
@Data
public class PeerSender
{

    private  int reConnectCount=0;
    private Channel channel;

    private ImNode rmNode;
    /**
     * 唯一标记
     */
    private boolean connectFlag = false;
    private UserDTO user;

    GenericFutureListener<ChannelFuture> closeListener = (ChannelFuture f) ->
    {
        log.info("分布式连接已经断开……{}", rmNode.toString());
        channel = null;
        connectFlag = false;
    };

    private GenericFutureListener<ChannelFuture> connectedListener = (ChannelFuture f) ->
    {
        final EventLoop eventLoop = f.channel().eventLoop();
        if (!f.isSuccess() && ++reConnectCount<3)
        {
            log.info("连接失败! 在10s之后准备尝试第{}次重连!",reConnectCount);
            eventLoop.schedule(() -> PeerSender.this.doConnect(), 10, TimeUnit.SECONDS);

            connectFlag = false;
        } else
        {
            connectFlag = true;

            log.info(new Date() + "分布式节点连接成功:{}", rmNode.toString());

            channel = f.channel();
            channel.closeFuture().addListener(closeListener);

            /**
             * 发送链接成功的通知
             */
            Notification<ImNode> notification = new Notification<>(ImWorker.getInst().getLocalNodeInfo());
            notification.setType(Notification.CONNECT_FINISHED);
            String json = JsonUtil.pojoToJson(notification);
            ProtoMsg.Message pkg = NotificationMsgBuilder.buildNotification(json);
            writeAndFlush(pkg);
        }
    };


    private Bootstrap b;
    private EventLoopGroup g;

    public PeerSender(ImNode n)
    {
        this.rmNode = n;

        /**
         * 客户端的是Bootstrap，服务端的则是 ServerBootstrap。
         * 都是AbstractBootstrap的子类。
         **/

        b = new Bootstrap();
        /**
         * 通过nio方式来接收连接和处理连接
         */

        g = new NioEventLoopGroup();


    }

    /**
     * 重连
     */
    public void doConnect()
    {

        // 服务器ip地址
        String host = rmNode.getHost();
        // 服务器端口
        int port = rmNode.getPort();

        try
        {
            if (b != null && b.group() == null)
            {
                b.group(g);
                b.channel(NioSocketChannel.class);
                b.option(ChannelOption.SO_KEEPALIVE, true);
                b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
                b.remoteAddress(host, port);

                // 设置通道初始化
                b.handler(
                        new ChannelInitializer<SocketChannel>()
                        {
                            public void initChannel(SocketChannel ch)
                            {
                                ch.pipeline().addLast("decoder", new ProtobufDecoder());
                                ch.pipeline().addLast("encoder", new ProtobufEncoder());
                                ch.pipeline().addLast("imNodeHeartBeatClientHandler", new ImNodeHeartBeatClientHandler());
                                ch.pipeline().addLast("exceptionHandler", new ImNodeExceptionHandler());
                            }
                        }
                );
                log.info(new Date() + "开始连接分布式节点:{}", rmNode.toString());

                ChannelFuture f = b.connect();
                f.addListener(connectedListener);


                // 阻塞
//                 f.channel().closeFuture().sync();
            } else if (b.group() != null)
            {
                log.info(new Date() + "再一次开始连接分布式节点", rmNode.toString());
                ChannelFuture f = b.connect();
                f.addListener(connectedListener);
            }
        } catch (Exception e)
        {
            log.info("客户端连接失败!" + e.getMessage());
        }

    }

    public void stopConnecting()
    {
        g.shutdownGracefully();
        connectFlag = false;
    }

    public void writeAndFlush(Object pkg)
    {
        if (connectFlag == false)
        {
            log.error("分布式节点未连接:", rmNode.toString());
            return;
        }
        channel.writeAndFlush(pkg);
    }


}
