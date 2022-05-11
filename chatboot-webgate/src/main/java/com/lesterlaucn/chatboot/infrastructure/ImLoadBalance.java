package com.lesterlaucn.chatboot.infrastructure;

import com.lesterlaucn.chatboot.constants.ServerConstants;
import com.lesterlaucn.chatboot.entity.ImNode;
import com.lesterlaucn.chatboot.util.JsonUtil;
import com.lesterlaucn.chatboot.zk.CuratorZKclient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * create by lesterlaucn
 **/
@Data
@Slf4j
public class ImLoadBalance {

    //Zk客户端
    private CuratorFramework client = null;
    private String managerPath;


    public ImLoadBalance(CuratorZKclient curatorZKClient) {
        this.client = curatorZKClient.getClient();
        managerPath = ServerConstants.MANAGE_PATH;
    }

    /**
     * 获取负载最小的IM节点
     *
     * @return
     */
    public ImNode getBestWorker() {
        List<ImNode> workers = getWorkers();

        log.info("全部节点如下：");
        workers.stream().forEach(node ->
        {
            log.info("节点信息：{}", JsonUtil.pojoToJson(node));
        });
        ImNode best = balance(workers);
        return best;
    }

    /**
     * 按照负载排序
     *
     * @param items 所有的节点
     * @return 负载最小的IM节点
     */
    protected ImNode balance(List<ImNode> items) {
        if (items.size() > 0) {
            // 根据balance值由小到大排序
            Collections.sort(items);
            // 返回balance值最小的那个
            ImNode node = items.get(0);
            log.info("最佳的节点为：{}", JsonUtil.pojoToJson(node));
            return node;
        } else {
            return null;
        }
    }

    /**
     * 从zookeeper中拿到所有IM节点
     */
    public List<ImNode> getWorkers() {

        List<ImNode> workers = new ArrayList<ImNode>();

        List<String> children = null;
        try {
            children = client.getChildren().forPath(managerPath);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        for (String child : children) {
            log.info("child:", child);
            byte[] payload = null;
            try {
                payload = client.getData().forPath(managerPath + "/" + child);

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (null == payload) {
                continue;
            }
            ImNode node = JsonUtil.jsonBytes2Object(payload, ImNode.class);
            node.setId(getIdByPath(child));

            workers.add(node);
        }
        return workers;

    }

    /**
     * 取得IM 节点编号
     *
     * @param path 路径
     * @return 编号
     */
    public long getIdByPath(String path) {
        String sid = null;
        if (null == path) {
            throw new RuntimeException("节点路径有误");
        }
        int index = path.lastIndexOf(ServerConstants.PATH_PREFIX_NO_STRIP);
        if (index >= 0) {
            index += ServerConstants.PATH_PREFIX_NO_STRIP.length();
            sid = index <= path.length() ? path.substring(index) : null;
        }

        if (null == sid) {
            throw new RuntimeException("节点ID获取失败");
        }

        return Long.parseLong(sid);

    }

    /**
     * 从zookeeper中删除所有IM节点
     */
    public void removeWorkers() {
        try {
            client.delete().deletingChildrenIfNeeded().forPath(managerPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}