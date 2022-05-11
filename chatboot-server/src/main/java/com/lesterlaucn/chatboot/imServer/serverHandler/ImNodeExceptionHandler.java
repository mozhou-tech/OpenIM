package com.lesterlaucn.chatboot.imServer.serverHandler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * create by lesterlaucn
 **/
@Slf4j
@ChannelHandler.Sharable
public class ImNodeExceptionHandler extends ChannelInboundHandlerAdapter
{

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        // ..

        //捕捉异常信息
        cause.printStackTrace();
        log.error(cause.getMessage());
        ctx.close();


    }

    /**
     * 通道 Read 读取 Complete 完成
     * 做刷新操作 ctx.flush()
     */
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception
    {
        ctx.flush();
    }

}