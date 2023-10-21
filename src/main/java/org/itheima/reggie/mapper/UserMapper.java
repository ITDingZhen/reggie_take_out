package org.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.itheima.reggie.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
