package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.entity.dto.OrdersDto;

public interface OrderService extends IService<Orders> {

//    void update(Long id, Integer status);

    void update(Orders orders);

    void submit(Orders orders);


    void again(Orders orders);
}
