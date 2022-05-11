/**
 * Created by lesterlaucn
 */

package com.lesterlaucn.chatboot.service;

import com.lesterlaucn.chatboot.infrastructure.mybatis.entity.UserPO;
import com.lesterlaucn.chatboot.infrastructure.mybatis.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    public UserPO login(UserPO user) {
 /*       User sample = new User();
        sample.setUserName(user.getUserName());
        User u = userMapper.selectOne(sample);
        if (null == u) {
            log.info("找不到用户信息 username={}", user.getUserName());

            return null;

        }
*/
        //为了简化演示，去掉数据库的部分

        return user;
    }

    @Cacheable(value = "chatboot:User:", key = "#userid")
    public UserPO getById(String userid) {
        //为了简化演示，去掉数据库的部分
       /* User u = userMapper.selectByPrimaryKey(Integer.valueOf(userid));
        if (null == u) {
            log.info("找不到用户信息 userid={}", userid);
        }
        return u;
  */
        return null;
    }

    @CacheEvict(value = "chatboot:User:", key = "#userid")
    public int deleteById(String userid) {
        //为了简化演示，去掉数据库的部分
/*        int u = userMapper.deleteByPrimaryKey(Integer.valueOf(userid));
        if (0 == u) {
            log.info("找不到用户信息 userid={}", userid);
        }
        return u;*/
        return 0;
    }

}
