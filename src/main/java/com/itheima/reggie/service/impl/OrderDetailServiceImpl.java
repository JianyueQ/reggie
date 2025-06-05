package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.mapper.OrderDetailMapper;
import com.itheima.reggie.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Override
    public List<OrderDetail> getByOrderId(Long orderId) {
        return orderDetailMapper.getByOrderId(orderId);
    }

    @Override
    public List<OrderDetail> listByOrderIds(List<Long> orderIds) {
        //如果orderIds不为空
        if (orderIds != null && orderIds.size() > 0){
            return orderDetailMapper.listByOrderIds(orderIds);
        }else {
            return null;
        }

    }

    @Override
    public List<OrderDetail> listByOrderId(Long id) {
        return orderDetailMapper.listByOrderId(id);
    }


}
