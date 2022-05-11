package com.lesterlaucn.chatboot.imServer.serverProcesser;


import com.lesterlaucn.chatboot.common.bean.msg.ProtoMsg;
import com.lesterlaucn.chatboot.imServer.server.session.LocalSession;

/**
 * 操作类
 */
public interface ServerReciever
{

    ProtoMsg.HeadType op();

    Boolean action(LocalSession ch, ProtoMsg.Message proto);

}
