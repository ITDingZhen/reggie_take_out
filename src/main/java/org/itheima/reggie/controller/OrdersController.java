package org.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.itheima.reggie.common.BaseContext;
import org.itheima.reggie.common.R;
import org.itheima.reggie.entity.OrderDetail;
import org.itheima.reggie.entity.Orders;
import org.itheima.reggie.entity.ShoppingCart;
import org.itheima.reggie.service.OrderDetailService;
import org.itheima.reggie.service.OrdersService;
import org.itheima.reggie.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        ordersService.submit(orders);
        return R.success("下单成功");
    }

    /*************************************************自己写的*****************************************************/

    /**
     * todo 在管理端查询订单的状态，里面的if有待修改，未完成
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page<Orders>> employeePage(int page, int pageSize, Long number, String beginTime,String endTime) {

        Page page1=new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> queryWrapper =new LambdaQueryWrapper<>();
        if(number!=null){
            queryWrapper.like(StringUtils.isNotEmpty(String.valueOf(number)),Orders::getId,number);
        }
        if (beginTime!=null&&endTime!=null){
            queryWrapper.between(StringUtils.isNotEmpty(String.valueOf(beginTime))||StringUtils.isNotEmpty(String.valueOf(beginTime)),Orders::getOrderTime,beginTime,endTime);
        }
          ordersService.page(page1,queryWrapper);

        return R.success(page1);
    }

    /**
     * 用户端打开页面查看订单
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> employeePage(int page, int pageSize) {
        Page page1=new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        ordersService.page(page1,queryWrapper);

        return R.success(page1);
    }

    /**
     * 在管理端完成配送
     * @param orders
     * @return
     */
    @PutMapping
    public R<String> updateStatus(@RequestBody Orders orders){
        LambdaQueryWrapper<Orders> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getId,orders.getId());
        Orders one = ordersService.getOne(queryWrapper);
        one.setStatus(orders.getStatus());
        ordersService.updateById(one);
        return R.success("测试");
    }


    /**
     *
     * @param orders
     * @return
     */
    @PostMapping("/again")
    public R<String> saveAgain(@RequestBody Orders orders){
     /*   LambdaQueryWrapper<Orders> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getId,orders.getId());
        Orders one = ordersService.getOne(queryWrapper);

        LambdaQueryWrapper<OrderDetail> queryWrapper1=new LambdaQueryWrapper<>();
        queryWrapper1.eq(OrderDetail::getOrderId,one.getId());

        List<OrderDetail> list = orderDetailService.list(queryWrapper1);
        for(OrderDetail orderDetail:list){
            ShoppingCart shoppingCart=new ShoppingCart();
            shoppingCart.setUserId(one.getUserId());
            shoppingCart.setDishId(orderDetail.getDishId());
            shoppingCart.setDishFlavor(orderDetail.getDishFlavor());
            shoppingCart.setNumber(orderDetail.getNumber());
            shoppingCart.setAmount(orderDetail.getAmount());//这个地方是单价
            shoppingCart.setSetmealId(orderDetail.getSetmealId());
            shoppingCart.setImage(orderDetail.getImage());
            shoppingCart.setName(orderDetail.getName());
            shoppingCartService.save(shoppingCart);
        }


*/
        return R.success("测试");
    }

}
