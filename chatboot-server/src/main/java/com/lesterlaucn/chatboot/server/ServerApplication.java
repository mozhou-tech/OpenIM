package com.lesterlaucn.chatboot.server;

import com.lesterlaucn.chatboot.server.server.ChatServer;
import com.lesterlaucn.chatboot.server.server.session.service.SessionManger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

@Configuration
//自动加载配置信息
//使包路径下带有@Value的注解自动注入
//使包路径下带有@Autowired的类可以自动注入
@SpringBootApplication
public class ServerApplication {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // 启动并初始化 Spring 环境及其各 Spring 组件
        ApplicationContext context =
                SpringApplication.run(ServerApplication.class, args);
        /**
         * 将SessionManger 单例设置为spring bean
         */
        SessionManger sessionManger = context.getBean(SessionManger.class);
        SessionManger.setSingleInstance(sessionManger);

        /**
         * 启动服务
         */
        ChatServer nettyServer = context.getBean(ChatServer.class);
        nettyServer.run();


    }

}
