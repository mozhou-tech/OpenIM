package com.lesterlaucn.chatboot.imServer.serverHandler;


import com.lesterlaucn.chatboot.common.bean.msg.ProtoMsg;
import com.lesterlaucn.chatboot.imServer.distributed.ImWorker;
import com.lesterlaucn.chatboot.util.JsonUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
@ChannelHandler.Sharable
public class ImNodeHeartBeatClientHandler extends ChannelInboundHandlerAdapter {

    String from = null;

    int seq = 0;

    //心跳的时间间隔，单位为s
    private static final int HEARTBEAT_INTERVAL = 50;

    public ProtoMsg.Message buildMessageHeartBeat() {
        if (null == from) {
            from = JsonUtil.pojoToJson(ImWorker.getInst().getLocalNode());
        }

        ProtoMsg.Message.Builder mb = ProtoMsg.Message.newBuilder()
                .setType(ProtoMsg.HeadType.HEART_BEAT)  //设置消息类型
                .setSequence(++seq);                 //设置应答流水，与请求对应
        ProtoMsg.MessageHeartBeat.Builder heartBeat =
                ProtoMsg.MessageHeartBeat.newBuilder()
                        .setSeq(seq)
                        .setJson(from)
                        .setUid("-1");
        mb.setHeartBeat(heartBeat.build());
        return mb.build();
    }

    //在Handler被加入到Pipeline时，开始发送心跳
    @Override
    public void handlerAdded(ChannelHandlerContext ctx)
            throws Exception {

        //发送心跳
        heartBeat(ctx);
    }

    //使用定时器，发送心跳报文
    public void heartBeat(ChannelHandlerContext ctx) {

        ProtoMsg.Message message = buildMessageHeartBeat();

        ctx.executor().schedule(() ->
        {

            if (ctx.channel().isActive()) {
                log.info(" 发送 ImNode HEART_BEAT  消息 other");
                ctx.writeAndFlush(message);

                //递归调用，发送下一次的心跳
                heartBeat(ctx);
            }

        }, HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
    }

    /**
     * 接受到服务器的心跳回写
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //判断消息实例
        if (null == msg || !(msg instanceof ProtoMsg.Message)) {
            super.channelRead(ctx, msg);
            return;
        }

        //判断类型
        ProtoMsg.Message pkg = (ProtoMsg.Message) msg;
        ProtoMsg.HeadType headType = pkg.getType();
        if (headType.equals(ProtoMsg.HeadType.HEART_BEAT)) {
            ProtoMsg.MessageHeartBeat messageHeartBeat = pkg.getHeartBeat();
            log.info("  收到 imNode HEART_BEAT  消息 from: " + messageHeartBeat.getJson());
            log.info("  收到 imNode HEART_BEAT seq: " + messageHeartBeat.getSeq());

            return;
        } else {
            super.channelRead(ctx, msg);

        }

    }

}
