package com.lesterlaucn.chatboot.infrastructure.mybatis.mapper;

import com.lesterlaucn.chatboot.infrastructure.mybatis.entity.UserPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<UserPO>
{
}