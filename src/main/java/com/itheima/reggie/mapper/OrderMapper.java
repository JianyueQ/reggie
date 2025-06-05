package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.entity.dto.OrdersDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
//    void update(Long id, Integer status);
    void update(Orders orders);

    void insertOrdersDto(OrdersDto ordersDto);


}
