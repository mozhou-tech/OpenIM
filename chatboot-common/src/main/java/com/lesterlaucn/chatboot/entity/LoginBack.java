package com.lesterlaucn.chatboot.entity;

import com.lesterlaucn.chatboot.common.bean.UserDTO;
import lombok.Data;

import java.util.List;

/**
 * create by lesterlaucn
 **/
@Data
public class LoginBack
{

    List<ImNode> imNodeList;

    private String token;

    private UserDTO userDTO;

}
