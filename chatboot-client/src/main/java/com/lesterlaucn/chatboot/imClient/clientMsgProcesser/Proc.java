package com.lesterlaucn.chatboot.imClient.clientMsgProcesser;


import com.lesterlaucn.chatboot.common.bean.msg.ProtoMsg;
import com.lesterlaucn.chatboot.imClient.client.ClientSession;

/**
 * 操作类
 */
public interface Proc
{

    ProtoMsg.HeadType op();

    void action(ClientSession ch, ProtoMsg.Message proto) throws Exception;

}
