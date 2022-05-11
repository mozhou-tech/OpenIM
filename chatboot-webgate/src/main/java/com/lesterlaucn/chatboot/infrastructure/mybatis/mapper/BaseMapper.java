package com.lesterlaucn.chatboot.infrastructure.mybatis.mapper;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author crazymakercircle
 */
public interface BaseMapper<T> extends Mapper<T>, MySqlMapper<T>
{

}
