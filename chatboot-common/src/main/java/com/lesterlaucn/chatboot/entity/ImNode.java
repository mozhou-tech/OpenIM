package com.lesterlaucn.chatboot.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * IM节点的POJO类
 * create by lesterlaucn
 **/
@Data
public class ImNode implements Comparable<ImNode>, Serializable {
    private static final long serialVersionUID = -499010884211304846L;

    //worker 的Id,zookeeper负责生成
    private long id;

    //Netty 服务 的连接数
    private Integer balance = 0;

    //Netty 服务 IP
    private String host = "127.0.0.1";

    //Netty 服务 端口
    private Integer port = 8081;

    public ImNode() {
    }

    public ImNode(String host, Integer port) {
        this.host = host;
        this.port = port;
    }


    @Override
    public String toString() {
        return "ImNode{" +
                "id='" + id + '\'' +
                "host='" + host + '\'' +
                ", port='" + port + '\'' +
                ",balance=" + balance +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImNode node = (ImNode) o;
//        return id == node.id &&
        return Objects.equals(host, node.host) &&
                Objects.equals(port, node.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, host, port);
    }

    /**
     * 升序排列
     */
    public int compareTo(ImNode o) {
        int weight1 = this.balance;
        int weight2 = o.balance;
        if (weight1 > weight2) {
            return 1;
        } else if (weight1 < weight2) {
            return -1;
        }
        return 0;
    }


    public void incrementBalance() {
        balance++;
    }

    public void decrementBalance() {
        balance--;
    }
}