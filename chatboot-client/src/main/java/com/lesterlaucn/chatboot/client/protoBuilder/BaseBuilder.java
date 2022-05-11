package com.lesterlaucn.chatboot.client.protoBuilder;

import com.lesterlaucn.chatboot.client.client.ClientSession;
import com.lesterlaucn.chatboot.common.bean.msg.ProtoMsg;

/**
 * 基础 Builder
 *
 * @author lesterlaucn
 */
public class BaseBuilder
{
    protected ProtoMsg.HeadType type;
    private long seqId;
    private ClientSession session;

    public BaseBuilder(ProtoMsg.HeadType type, ClientSession session)
    {
        this.type = type;
        this.session = session;
    }

    /**
     * 构建消息 基础部分
     */
    public ProtoMsg.Message buildCommon(long seqId)
    {
        this.seqId = seqId;

        ProtoMsg.Message.Builder mb =
                ProtoMsg.Message
                        .newBuilder()
                        .setType(type)
                        .setSessionId(session.getSessionId())
                        .setSequence(seqId);
        return mb.buildPartial();
    }

}
