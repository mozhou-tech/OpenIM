package com.lesterlaucn.chatboot.client.feignClient;

import feign.Param;
import feign.RequestLine;

/**
 * 远程接口的本地代理
 * Created by lesterlaucn
 */
public interface UserAction
{

    /**
     * 登录代理
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录结果
     */
    @RequestLine("GET /user/login/{username}/{password}")
    public String loginAction(
            @Param("username") String username,
            @Param("password") String password);


    /**
     * 获取用户信息代理
     *
     * @param userid 用户id
     * @return 用户信息
     */
    @RequestLine("GET /{userid}")
    public String getById(@Param("userid") Integer userid);


}
