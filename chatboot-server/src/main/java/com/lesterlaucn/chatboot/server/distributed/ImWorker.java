package com.lesterlaucn.chatboot.server.distributed;

import com.lesterlaucn.chatboot.constants.ServerConstants;
import com.lesterlaucn.chatboot.entity.ImNode;
import com.lesterlaucn.chatboot.util.JsonUtil;
import com.lesterlaucn.chatboot.zk.CuratorZKclient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * IM 节点的ZK协调客户端
 * create by lesterlaucn
 **/
@Data
@Slf4j
public class ImWorker {

    //Zk curator 客户端
    private CuratorFramework client = null;

    //保存当前Znode节点的路径，创建后返回
    private String pathRegistered = null;

    private ImNode localNode = null;

    private static ImWorker singleInstance = null;
    private boolean inited = false;

    //取得单例
    public synchronized static ImWorker getInst() {

        if (null == singleInstance) {

            singleInstance = new ImWorker();
            singleInstance.localNode = new ImNode();
        }
        return singleInstance;
    }

    private ImWorker() {

    }

    // 在zookeeper中创建临时节点
    public synchronized void init() {

        if (inited) {
            return;
        }
        inited = true;
        if (null == client) {
            this.client = CuratorZKclient.instance.getClient();
        }
        if (null == localNode) {
            localNode = new ImNode();
        }

        deleteWhenHasNoChildren(ServerConstants.MANAGE_PATH);

        createParentIfNeeded(ServerConstants.MANAGE_PATH);

        // 创建一个 ZNode 节点
        // 节点的 payload 为当前worker 实例

        try {
            byte[] payload = JsonUtil.object2JsonBytes(localNode);

            pathRegistered = client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(ServerConstants.PATH_PREFIX, payload);

            //为node 设置id
            localNode.setId(getId());
            log.info("本地节点, path={}, id={}", pathRegistered, localNode.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setLocalNode(String ip, int port) {
        localNode.setHost(ip);
        localNode.setPort(port);
    }

    /**
     * 取得IM 节点编号
     *
     * @return 编号
     */
    public long getId() {

        return getIdByPath(pathRegistered);

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
        int index = path.lastIndexOf(ServerConstants.PATH_PREFIX);
        if (index >= 0) {
            index += ServerConstants.PATH_PREFIX.length();
            sid = index <= path.length() ? path.substring(index) : null;
        }

        if (null == sid) {
            throw new RuntimeException("节点ID获取失败");
        }

        return Long.parseLong(sid);

    }


    /**
     * 增加负载，表示有用户登录成功
     *
     * @return 成功状态
     */
    public boolean incBalance() {
        if (null == localNode) {
            throw new RuntimeException("还没有设置Node 节点");
        }
        // 增加负载：增加负载，并写回zookeeper
        while (true) {
            try {
                localNode.incrementBalance();
                byte[] payload = JsonUtil.object2JsonBytes(localNode);
                client.setData().forPath(pathRegistered, payload);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

    }

    /**
     * 减少负载，表示有用户下线，写回zookeeper
     *
     * @return 成功状态
     */
    public boolean decrBalance() {
        if (null == localNode) {
            throw new RuntimeException("还没有设置Node 节点");
        }
        while (true) {
            try {

                localNode.decrementBalance();

                byte[] payload = JsonUtil.object2JsonBytes(localNode);
                client.setData().forPath(pathRegistered, payload);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

    }

    /**
     * 创建父节点
     *
     * @param managePath 父节点路径
     */
    private void createParentIfNeeded(String managePath) {

        try {
            Stat stat = client.checkExists().forPath(managePath);
            if (null == stat) {
                client.create()
                        .creatingParentsIfNeeded()
                        .withProtection()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath(managePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 返回本地的节点信息
     *
     * @return 本地的节点信息
     */
    public ImNode getLocalNodeInfo() {
        return localNode;
    }


    //删除该路径
    public boolean delPath(String path) {
        boolean b = false;

        //检测是否存在该路径。
        try {
            Void stat = client.delete().forPath(path);
            b = stat == null ? false : true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return b;
    }

    //获取子节点
    public List<String> getChildren(String path) {

        //检测是否存在该路径。
        try {
            List<String> children = client.getChildren().forPath(path);
            return children;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void deleteWhenHasNoChildren(String path) {

        int index = path.lastIndexOf("/");

        String parent = path.substring(0, index);

        boolean exist = isNodeExist(parent);
        if (exist) {
            List<String> children = getChildren(parent);
            if (null != children && children.size() == 0) {
                delPath(parent);
                log.info("删除空的 父节点:" + parent);

            }
        }
    }

    /**
     * 检查节点
     */
    public boolean isNodeExist(String zkPath) {
        try {

            Stat stat = client.checkExists().forPath(zkPath);
            if (null == stat) {
                log.info("节点不存在:", zkPath);
                return false;
            } else {

                log.info("节点存在 stat is:", stat.toString());
                return true;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    static {
        //JVM关闭时的钩子函数
        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> {
                    ImWorker.getInst().deleteNode();
                }, "关掉worker，删除zk节点"));
    }

    private void deleteNode() {
        log.info("删除 worker node, path={}, id={}", pathRegistered, localNode.getId());
        try {

            Stat stat = client.checkExists().forPath(pathRegistered);
            if (null == stat) {
                log.info("节点不存在:{}", pathRegistered);
            } else {

                client.delete().forPath(pathRegistered);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}