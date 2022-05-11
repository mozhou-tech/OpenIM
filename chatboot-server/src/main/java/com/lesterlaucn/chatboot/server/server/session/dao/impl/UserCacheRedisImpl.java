package com.lesterlaucn.chatboot.server.server.session.dao.impl;


import com.lesterlaucn.chatboot.server.server.session.dao.UserCacheDAO;
import com.lesterlaucn.chatboot.server.server.session.entity.SessionCache;
import com.lesterlaucn.chatboot.server.server.session.entity.UserCache;
import com.lesterlaucn.chatboot.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * create by lesterlaucn
 **/
@Repository("UserCacheRedisImpl")
public class UserCacheRedisImpl implements UserCacheDAO
{

    public static final String REDIS_PREFIX = "UserCache:uid:";
    @Autowired
    protected StringRedisTemplate stringRedisTemplate;
    private static final long CASHE_LONG = 60 * 4;//4小时之后，得重新登录


    @Override
    public void save(final UserCache uss)
    {
        String key = REDIS_PREFIX + uss.getUserId();
        String value = JsonUtil.pojoToJson(uss);
        stringRedisTemplate.opsForValue().set(key, value, CASHE_LONG, TimeUnit.MINUTES);
    }


    @Override
    public UserCache get(final String usID)
    {
        String key = REDIS_PREFIX + usID;
        String value = (String) stringRedisTemplate.opsForValue().get(key);

        if (!StringUtils.isEmpty(value))
        {
            return JsonUtil.jsonToPojo(value, UserCache.class);
        }
        return null;
    }


    @Override
    public void addSession(String uid, SessionCache session)
    {
        UserCache us = get(uid);
        if (null == us)
        {
            us = new UserCache(uid);
        }

        us.addSession(session);
        save(us);
    }

    @Override
    public void removeSession(String uid, String sessionId)
    {
        UserCache us = get(uid);
        if (null == us)
        {
            us = new UserCache(uid);
        }
        us.removeSession(sessionId);
        save(us);
    }

}