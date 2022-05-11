package com.lesterlaucn.chatboot.server.server.session.entity;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * create by lesterlaucn
 **/
@Data
public class UserCache
{

    private String userId;
    private Map<String, SessionCache> map = new LinkedHashMap<>(10);

    public UserCache(String userId)
    {
        this.userId = userId;
    }

    //为用户增加session
    public void addSession(SessionCache session)
    {

        map.put(session.getSessionId(), session);
    }

    //为用户移除session
    public void removeSession(String sessionId)
    {
        map.remove(sessionId);
    }


}
