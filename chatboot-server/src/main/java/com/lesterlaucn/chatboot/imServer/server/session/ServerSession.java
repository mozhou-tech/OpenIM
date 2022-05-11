package com.lesterlaucn.chatboot.imServer.server.session;

/**
 * create by lesterlaucn
 **/
public interface ServerSession
{
    void writeAndFlush(Object pkg);

    String getSessionId();

    boolean isValid();

    /**
     * 获取用户id
     * @return  用户id
     */
    String getUserId();
}

