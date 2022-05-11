package com.lesterlaucn.chatboot.server.serverProcesser;


import com.lesterlaucn.chatboot.protoc.msg.ProtoMsg;
import com.lesterlaucn.chatboot.server.server.session.LocalSession;

/**
 * 操作类
 */
public interface ServerReciever
{

    ProtoMsg.HeadType op();

    Boolean action(LocalSession ch, ProtoMsg.Message proto);

}
