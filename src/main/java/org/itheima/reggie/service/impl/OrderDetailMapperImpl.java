package org.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.itheima.reggie.entity.OrderDetail;
import org.itheima.reggie.mapper.OrderDetailMapper;
import org.itheima.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderDetailMapperImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {


}
