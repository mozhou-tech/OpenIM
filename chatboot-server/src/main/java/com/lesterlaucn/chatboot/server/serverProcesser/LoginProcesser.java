package com.lesterlaucn.chatboot.server.serverProcesser;

import com.lesterlaucn.chatboot.protoc.ProtoInstant;
import com.lesterlaucn.chatboot.protoc.UserDTO;
import com.lesterlaucn.chatboot.protoc.message.ProtoMsg;
import com.lesterlaucn.chatboot.server.protoBuilder.LoginResponceBuilder;
import com.lesterlaucn.chatboot.server.server.session.LocalSession;
import com.lesterlaucn.chatboot.server.server.session.service.SessionManger;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Data
@Slf4j
@Service("LoginProcesser")
public class LoginProcesser extends AbstractServerProcesser
{
    @Autowired
    LoginResponceBuilder loginResponceBuilder;
    @Autowired
    SessionManger sessionManger;

    @Override
    public ProtoMsg.HeadType op()
    {
        return ProtoMsg.HeadType.LOGIN_REQUEST;
    }

    @Override
    public Boolean action(LocalSession session,
                          ProtoMsg.Message proto)
    {
        // 取出token验证
        ProtoMsg.LoginRequest info = proto.getLoginRequest();
        long seqNo = proto.getSequence();

        UserDTO user = UserDTO.fromMsg(info);

        //检查用户
        boolean isValidUser = checkUser(user);
        if (!isValidUser)
        {
            ProtoInstant.ResultCodeEnum resultcode =
                    ProtoInstant.ResultCodeEnum.NO_TOKEN;
            ProtoMsg.Message response =
                    loginResponceBuilder.loginResponce(resultcode, seqNo, "-1");
            //发送之后，断开连接
            session.writeAndClose(response);
            return false;
        }

        session.setUser(user);

        /**
         * 绑定session
         */
        session.bind();
        sessionManger.addLocalSession(session);


        /**
         * 通知客户端：登录成功
         */

        ProtoInstant.ResultCodeEnum resultcode = ProtoInstant.ResultCodeEnum.SUCCESS;
        ProtoMsg.Message response =
                loginResponceBuilder.loginResponce(resultcode, seqNo, session.getSessionId());
        session.writeAndFlush(response);
        return true;
    }

    private boolean checkUser(UserDTO user)
    {

        //校验用户,比较耗时的操作,需要100 ms以上的时间
        //方法1：调用远程用户restfull 校验服务
        //方法2：调用数据库接口校验

//        List<ServerSession> l = sessionManger.getSessionsBy(user.getUserId());
//
//
//        if (null != l && l.size() > 0)
//        {
//            return false;
//        }

        return true;

    }

}
