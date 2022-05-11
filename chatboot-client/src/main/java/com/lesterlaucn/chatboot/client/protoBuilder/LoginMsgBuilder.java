/**
 * Created by lesterlaucn
 */

package com.lesterlaucn.chatboot.client.protoBuilder;

import com.lesterlaucn.chatboot.client.client.ClientSession;
import com.lesterlaucn.chatboot.common.bean.UserDTO;
import com.lesterlaucn.chatboot.common.bean.msg.ProtoMsg;


/**
 * 登陆消息Builder
 */
public class LoginMsgBuilder extends BaseBuilder
{
    private final UserDTO user;

    public LoginMsgBuilder(UserDTO user, ClientSession session)
    {
        super(ProtoMsg.HeadType.LOGIN_REQUEST, session);
        this.user = user;
    }

    public ProtoMsg.Message build()
    {
        ProtoMsg.Message message = buildCommon(-1);
        ProtoMsg.LoginRequest.Builder lb =
                ProtoMsg.LoginRequest.newBuilder()
                        .setDeviceId(user.getDevId())
                        .setPlatform(user.getPlatform().ordinal())
                        .setToken(user.getToken())
                        .setUid(user.getUserId());
        return message.toBuilder().setLoginRequest(lb).build();
    }

    public static ProtoMsg.Message buildLoginMsg(
            UserDTO user, ClientSession session)
    {
        LoginMsgBuilder builder =
                new LoginMsgBuilder(user, session);
        return builder.build();

    }
}


