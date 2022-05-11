package com.lesterlaucn.chatboot.client.clientMsgProcesser;


import com.lesterlaucn.chatboot.client.client.ClientSession;
import com.lesterlaucn.chatboot.protoc.msg.ProtoMsg;

/**
 * 操作类
 */
public interface Proc
{

    ProtoMsg.HeadType op();

    void action(ClientSession ch, ProtoMsg.Message proto) throws Exception;

}
