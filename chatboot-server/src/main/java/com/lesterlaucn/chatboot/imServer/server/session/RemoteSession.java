package com.lesterlaucn.chatboot.imServer.server.session;

import com.lesterlaucn.chatboot.entity.ImNode;
import com.lesterlaucn.chatboot.imServer.distributed.PeerSender;
import com.lesterlaucn.chatboot.imServer.distributed.WorkerRouter;
import com.lesterlaucn.chatboot.imServer.server.session.entity.SessionCache;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * create by lesterlaucn
 **/
@Data
@Builder
@AllArgsConstructor
public class RemoteSession implements ServerSession, Serializable
{
    private static final long serialVersionUID = -400010884211394846L;
    SessionCache cache;

    private boolean valid = true;


    public RemoteSession(SessionCache cache)
    {
        this.cache = cache;
    }

    /**
     * 通过远程节点，转发
     */
    @Override
    public void writeAndFlush(Object pkg)
    {
        ImNode imNode = cache.getImNode();
        long nodeId = imNode.getId();

        //获取转发的  sender
        PeerSender sender =
                WorkerRouter.getInst().route(nodeId);

        if(null!=sender)
        {
            sender.writeAndFlush(pkg);
        }
    }

    @Override
    public String getSessionId()
    {
        //委托
        return cache.getSessionId();
    }

    @Override
    public boolean isValid()
    {
        return valid;
    }

    public void setValid(boolean valid)
    {
        this.valid = valid;
    }

    public String getUserId()
    {
        //委托
        return cache.getUserId();
    }
}
