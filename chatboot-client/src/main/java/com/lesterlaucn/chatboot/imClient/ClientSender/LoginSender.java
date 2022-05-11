package com.lesterlaucn.chatboot.imClient.ClientSender;

import com.lesterlaucn.chatboot.im.common.bean.msg.ProtoMsg;
import com.lesterlaucn.chatboot.imClient.clientBuilder.LoginMsgBuilder;
import com.lesterlaucn.chatboot.util.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("LoginSender")
public class LoginSender extends BaseSender
{


    public void sendLoginMsg()
    {
        if (!isConnected())
        {
            log.info("还没有建立连接!");
            return;
        }
        Logger.tcfo("发送登录消息");
        ProtoMsg.Message message =
                LoginMsgBuilder.buildLoginMsg(getUser(), getSession());
        super.sendMsg(message);
    }


}
