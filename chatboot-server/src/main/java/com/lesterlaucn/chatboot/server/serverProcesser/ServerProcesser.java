package com.lesterlaucn.chatboot.server.serverProcesser;


import com.lesterlaucn.chatboot.protoc.message.ProtoMsg;
import com.lesterlaucn.chatboot.server.server.session.LocalSession;

/**
 * 操作类
 */
public interface ServerProcesser
{

    ProtoMsg.HeadType type();

    boolean action(LocalSession ch, ProtoMsg.Message proto);

}
