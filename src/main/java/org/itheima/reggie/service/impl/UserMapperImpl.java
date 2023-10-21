package org.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.itheima.reggie.entity.User;
import org.itheima.reggie.mapper.UserMapper;
import org.itheima.reggie.service.UserService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserMapperImpl extends ServiceImpl<UserMapper, User> implements UserService {


}
