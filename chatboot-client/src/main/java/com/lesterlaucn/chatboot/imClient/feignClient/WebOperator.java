package com.lesterlaucn.chatboot.imClient.feignClient;

import com.lesterlaucn.chatboot.constants.ServerConstants;
import com.lesterlaucn.chatboot.entity.LoginBack;
import com.lesterlaucn.chatboot.util.JsonUtil;
import feign.Feign;
import feign.codec.StringDecoder;

public class WebOperator
{

    public static LoginBack login(String userName, String password)
    {
        UserAction action = Feign.builder()
//                .decoder(new GsonDecoder())
                .decoder(new StringDecoder())
                .target(UserAction.class, ServerConstants.WEB_URL);

        String s = action.loginAction(userName, password);

        LoginBack back = JsonUtil.jsonToPojo(s, LoginBack.class);
        return back;

    }
}
