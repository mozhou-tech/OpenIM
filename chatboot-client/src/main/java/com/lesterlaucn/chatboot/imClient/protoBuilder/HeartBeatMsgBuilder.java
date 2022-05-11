/**
 * Created by lesterlaucn
 */

package com.lesterlaucn.chatboot.imClient.protoBuilder;

import com.lesterlaucn.chatboot.im.common.bean.UserDTO;
import com.lesterlaucn.chatboot.im.common.bean.msg.ProtoMsg;
import com.lesterlaucn.chatboot.imClient.client.ClientSession;


/**
 * 心跳消息Builder
 */
public class HeartBeatMsgBuilder extends BaseBuilder
{
    private final UserDTO user;

    public HeartBeatMsgBuilder(UserDTO user, ClientSession session)
    {
        super(ProtoMsg.HeadType.HEART_BEAT, session);
        this.user = user;
    }

    public ProtoMsg.Message buildMsg()
    {
        ProtoMsg.Message message = buildCommon(-1);
        ProtoMsg.MessageHeartBeat.Builder lb =
                ProtoMsg.MessageHeartBeat.newBuilder()
                        .setSeq(0)
                        .setJson("{\"from\":\"client\"}")
                        .setUid(user.getUserId());
        return message.toBuilder().setHeartBeat(lb).build();
    }


}


