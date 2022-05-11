/**
 * Created by lesterlaucn
 */

package com.lesterlaucn.chatboot.client.protoBuilder;

import com.lesterlaucn.chatboot.client.client.ClientSession;
import com.lesterlaucn.chatboot.protoc.UserDTO;
import com.lesterlaucn.chatboot.protoc.message.ProtoMsg;


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


