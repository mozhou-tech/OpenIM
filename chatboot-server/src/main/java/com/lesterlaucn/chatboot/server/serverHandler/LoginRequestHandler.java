package com.lesterlaucn.chatboot.server.serverHandler;

import com.lesterlaucn.chatboot.cocurrent.CallbackTask;
import com.lesterlaucn.chatboot.cocurrent.CallbackTaskScheduler;
import com.lesterlaucn.chatboot.protoc.msg.ProtoMsg;
import com.lesterlaucn.chatboot.server.server.session.LocalSession;
import com.lesterlaucn.chatboot.server.server.session.service.SessionManger;
import com.lesterlaucn.chatboot.server.serverProcesser.LoginProcesser;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("LoginRequestHandler")
@ChannelHandler.Sharable
public class LoginRequestHandler extends ChannelInboundHandlerAdapter
{

    @Autowired
    LoginProcesser loginProcesser;

    /**
     * 收到消息
     */
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception
    {
        if (null == msg || !(msg instanceof ProtoMsg.Message))
        {
            super.channelRead(ctx, msg);
            return;
        }

        ProtoMsg.Message pkg = (ProtoMsg.Message) msg;

        //取得请求类型
        ProtoMsg.HeadType headType = pkg.getType();

        if (!headType.equals(loginProcesser.op()))
        {
            super.channelRead(ctx, msg);
            return;
        }


        LocalSession session = new LocalSession(ctx.channel());

        //异步任务，处理登录的逻辑
        CallbackTaskScheduler.add(new CallbackTask<Boolean>()
        {
            @Override
            public Boolean execute() throws Exception
            {
                return loginProcesser.action(session, pkg);
            }

            //异步任务返回
            @Override
            public void onBack(Boolean r)
            {
                if (r)
                {
//                    ctx.pipeline().remove(LoginRequestHandler.this);
                    log.info("登录成功:" + session.getUser());

//                    ctx.pipeline().addAfter("login", "chat",   chatRedirectHandler);
                    ctx.pipeline().addAfter("login", "heartBeat",new HeartBeatServerHandler());
                    ctx.pipeline().remove("login");
                } else
                {
                    SessionManger.inst().closeSession(ctx);

                    log.info("登录失败:" + session.getUser());

                }

            }
            //异步任务异常

            @Override
            public void onException(Throwable t)
            {
                t.printStackTrace();
                log.info("登录失败:" + session.getUser());
                SessionManger.inst().closeSession(ctx);


            }
        });

    }


}
