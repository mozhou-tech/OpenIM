package com.lesterlaucn.chatboot.server.serverHandler;

import com.lesterlaucn.chatboot.cocurrent.FutureTaskScheduler;
import com.lesterlaucn.chatboot.protoc.msg.ProtoMsg;
import com.lesterlaucn.chatboot.server.server.session.LocalSession;
import com.lesterlaucn.chatboot.server.server.session.ServerSession;
import com.lesterlaucn.chatboot.server.server.session.service.SessionManger;
import com.lesterlaucn.chatboot.server.serverProcesser.ChatRedirectProcesser;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
@Services
@ChannelHandler.Sharable
public class ChatRedirectHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    ChatRedirectProcesser redirectProcesser;

    @Autowired
    SessionManger sessionManger;

    /**
     * 收到消息
     */
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        //判断消息实例
        if (null == msg || !(msg instanceof ProtoMsg.Message)) {
            super.channelRead(ctx, msg);
            return;
        }

        //判断消息类型
        ProtoMsg.Message pkg = (ProtoMsg.Message) msg;
        ProtoMsg.HeadType headType = ((ProtoMsg.Message) msg).getType();
        if (!headType.equals(redirectProcesser.op())) {
            super.channelRead(ctx, msg);
            return;
        }
        //异步处理转发的逻辑
        FutureTaskScheduler.add(() ->
        {

            //判断是否登录,如果登录了，则为用户消息
            LocalSession session = LocalSession.getSession(ctx);
            if (null != session && session.isLogin()) {

                redirectProcesser.action(session, pkg);
                return;
            }

            //没有登录，则为中转消息
            ProtoMsg.MessageRequest request = pkg.getMessageRequest();
            List<ServerSession> toSessions = SessionManger.inst().getSessionsBy(request.getTo());
            toSessions.forEach((serverSession) ->
            {

                if (serverSession instanceof LocalSession)
                // 将IM消息发送到接收方
                {
                    serverSession.writeAndFlush(pkg);

                }

            });


        });
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx)
            throws Exception {
        LocalSession session = ctx.channel().attr(LocalSession.SESSION_KEY).get();

        if (null != session && session.isValid()) {
            session.close();
            sessionManger.removeSession(session.getSessionId());
        }
    }
}
