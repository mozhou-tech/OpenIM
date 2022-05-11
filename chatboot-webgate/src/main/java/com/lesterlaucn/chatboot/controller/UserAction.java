package com.lesterlaucn.chatboot.controller;

import com.lesterlaucn.chatboot.controller.utility.BaseController;
import com.lesterlaucn.chatboot.entity.ImNode;
import com.lesterlaucn.chatboot.entity.LoginBack;
import com.lesterlaucn.chatboot.infrastructure.ImLoadBalance;
import com.lesterlaucn.chatboot.infrastructure.mybatis.entity.UserPO;
import com.lesterlaucn.chatboot.protoc.UserDTO;
import com.lesterlaucn.chatboot.service.UserService;
import com.lesterlaucn.chatboot.utils.JsonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * WEB GATE
 * Created by lesterlaucn
 */

//@EnableAutoConfiguration
@RestController
@RequestMapping(value = "/user", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api("User 相关的api")
public class UserAction extends BaseController
{
    @Resource
    private UserService userService;
    @Resource
    private ImLoadBalance imLoadBalance;

    /**
     * Web短连接登录
     *
     * @param username 用户名
     * @param password 命名
     * @return 登录结果
     */
    @ApiOperation(value = "登录", notes = "根据用户信息登录")
    @RequestMapping(value = "/login/{username}/{password}", method = RequestMethod.GET)
    public String loginAction(
            @PathVariable("username") String username,
            @PathVariable("password") String password)
    {
        UserPO user = new UserPO();
        user.setUserName(username);
        user.setPassWord(password);
        user.setUserId(user.getUserName());

//        User loginUser = userService.login(user);

        LoginBack back = new LoginBack();
        /**
         * 取得最佳的Netty服务器-->取得所有的节点
         */
        //ImNode bestWorker = imLoadBalance.getBestWorker();
        //back.setImNode(bestWorker);
        List<ImNode> allWorker = imLoadBalance.getWorkers();
        back.setImNodeList(allWorker);
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        back.setUserDTO(userDTO);
        back.setToken(user.getUserId().toString());
        String r = JsonUtil.pojoToJson(back);
        return r;
    }


    /**
     * 从zookeeper中删除所有IM节点
     *
     * @return 删除结果
     */
    @ApiOperation(value = "删除节点", notes = "从zookeeper中删除所有IM节点")
    @RequestMapping(value = "/removeWorkers", method = RequestMethod.GET)
    public String removeWorkers()
    {
        imLoadBalance.removeWorkers();
        return "已经删除";
    }

}