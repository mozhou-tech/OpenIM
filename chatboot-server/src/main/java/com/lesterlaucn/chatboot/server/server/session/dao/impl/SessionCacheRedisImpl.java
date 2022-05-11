package com.lesterlaucn.chatboot.server.server.session.dao.impl;


import com.lesterlaucn.chatboot.server.server.session.dao.SessionCacheDAO;
import com.lesterlaucn.chatboot.server.server.session.entity.SessionCache;
import com.lesterlaucn.chatboot.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * create by lesterlaucn
 **/
@Repository("SessionCacheRedisImpl")
public class SessionCacheRedisImpl implements SessionCacheDAO
{

    public static final String REDIS_PREFIX = "SessionCache:id:";
    @Autowired
    protected StringRedisTemplate stringRedisTemplate;
    private static final long CASHE_LONG = 60 * 4;//4小时之后，得重新登录


    @Override
    public void save(final SessionCache sessionCache)
    {
        String key = REDIS_PREFIX + sessionCache.getSessionId();
        String value = JsonUtil.pojoToJson(sessionCache);
        stringRedisTemplate.opsForValue().set(key, value, CASHE_LONG, TimeUnit.MINUTES);
    }


    @Override
    public SessionCache get(final String sessionId)
    {
        String key = REDIS_PREFIX + sessionId;
        String value = (String) stringRedisTemplate.opsForValue().get(key);

        if (!StringUtils.isEmpty(value))
        {
            return JsonUtil.jsonToPojo(value, SessionCache.class);
        }
        return null;
    }

  @Override
    public void remove( String sessionId)
    {
        String key = REDIS_PREFIX + sessionId;
        stringRedisTemplate.delete(key);
    }

}