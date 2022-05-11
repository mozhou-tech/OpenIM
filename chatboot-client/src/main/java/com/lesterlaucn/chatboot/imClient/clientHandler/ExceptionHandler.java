package com.lesterlaucn.chatboot.imClient.clientHandler;

import com.lesterlaucn.chatboot.im.common.exception.BusinessException;
import com.lesterlaucn.chatboot.im.common.exception.InvalidFrameException;
import com.lesterlaucn.chatboot.imClient.client.CommandController;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 异常处理器
 * create by lesterlaucn
 **/
@Slf4j
@ChannelHandler.Sharable
@Service("ExceptionHandler")
public class ExceptionHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    private CommandController commandController;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof BusinessException) {
            //业务异常，通知客户端
        } else if (cause instanceof InvalidFrameException) {
            //报文异常
            log.error(cause.getMessage());
            //服务器做适当处理
        } else {
            //其他异常
            //捕捉异常信息
//             cause.printStackTrace();
            log.error(cause.getMessage());
            ctx.close();

            //开始重连
            commandController.setConnectFlag(false);
            commandController.startConnectServer();
        }
    }

    /**
     * 通道 Read 读取 Complete 完成
     * 做刷新操作 ctx.flush()
     */
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

}