package com.lesterlaucn.chatboot.crazyIm;

import com.google.gson.reflect.TypeToken;
import com.lesterlaucn.chatboot.common.bean.Notification;
import com.lesterlaucn.chatboot.entity.ImNode;
import com.lesterlaucn.chatboot.utils.JsonUtil;
import org.junit.Test;

public class NotificationJsonConvert
{

    @Test
    public void convertFromJson()
    {
        ImNode imNode = new ImNode("unKnown", 111);;
        Notification<ImNode> notification = new Notification<ImNode>(imNode);
        notification.setType(Notification.CONNECT_FINISHED);
        String json = JsonUtil.pojoToJson(notification);


        Notification<ImNode> no2 = JsonUtil.jsonToPojo(json, new TypeToken<Notification<ImNode>>()
        {
        }.getType());

        System.out.println("no2 = " + no2);


    }

}
