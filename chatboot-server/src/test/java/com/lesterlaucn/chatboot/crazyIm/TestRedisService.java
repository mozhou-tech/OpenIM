package com.lesterlaucn.chatboot.crazyIm;

import com.lesterlaucn.chatboot.imServer.server.session.dao.SessionCacheDAO;
import com.lesterlaucn.chatboot.imServer.server.session.dao.UserCacheDAO;
import com.lesterlaucn.chatboot.imServer.server.session.entity.SessionCache;
import com.lesterlaucn.chatboot.imServer.server.session.entity.UserCache;
import com.lesterlaucn.chatboot.imServer.startup.ServerApplication;
import com.lesterlaucn.chatboot.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServerApplication.class)
@Slf4j
public class TestRedisService {

    @Autowired
    private UserCacheDAO userCacheDAO;
    @Autowired
    private SessionCacheDAO sessionCacheDAO;


    //测试用例： 测试 会话的 缓存 CRUD
    @Test
    public void testSaveSession() throws Exception {
        SessionCache cache=new SessionCache();
        cache.setSessionId("1");
        sessionCacheDAO.save(cache);

        SessionCache sessionCache2=  sessionCacheDAO.get("1");

        System.out.println("sessionCache2 = " + sessionCache2);
    }

    //测试用例： 测试 用户的 缓存 CRUD

    @Test
    public void testSaveUser() throws Exception {
        UserCache cache=new UserCache("2");
        SessionCache sessionCache=new SessionCache();
        sessionCache.setSessionId("1");

        cache.addSession(sessionCache);
        userCacheDAO.save(cache);

        UserCache userCache=  userCacheDAO.get("2");

        System.out.println("userCache = " + JsonUtil.pojoToJson(userCache));
    }

}
