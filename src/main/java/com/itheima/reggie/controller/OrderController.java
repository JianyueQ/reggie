package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.constext.BaseContext;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.entity.dto.OrdersDto;
import com.itheima.reggie.service.OrderDetailService;
import com.itheima.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 查询订单列表
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize,Long number ,String beginTime,String endTime){

        Page<Orders> pageInfo = new Page<>(page,pageSize);
        Page<OrdersDto> pageInfoDto = new Page<>();
        //构造条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(number != null,Orders::getNumber,number);
        queryWrapper.between(beginTime != null && endTime != null,Orders::getOrderTime,beginTime,endTime);
        //添加排序条件
        queryWrapper.orderByDesc(Orders::getOrderTime);
        //执行查询
        orderService.page(pageInfo,queryWrapper);

        List<Orders> records = pageInfo.getRecords();


        // 一次性获取所有 order detail
        List<Long> orderIds = records.stream().map(Orders::getId).collect(Collectors.toList());
        List<OrderDetail> allDetails = orderDetailService.listByOrderIds(orderIds);

        // 按 orderId 分组
        Map<Long, List<OrderDetail>> detailMap = allDetails.stream().collect(Collectors.groupingBy(OrderDetail::getOrderId));

        List<OrdersDto> list = new ArrayList<>();

        for (Orders orders : records){
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(orders,ordersDto);
            ordersDto.setOrderDetails(detailMap.get(orders.getId()));
            list.add(ordersDto);
        }

        pageInfoDto.setRecords(list);
        pageInfoDto.setTotal(pageInfo.getTotal());
        return R.success(pageInfoDto);
    }

    /**
     * 取消，派送，完成
     */
    @PutMapping
    public R<String> update(@RequestBody Orders orders){
        log.info("修改订单状态：{}",orders.toString());
        orderService.update(orders);
        return R.success("修改成功");
    }

    /**
     * 添加订单
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("用户下单：{}",orders);
        orderService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 用户信息分页查询
     */
    @GetMapping("/userPage")
    public R<Page> userPage(int page,int pageSize){
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>();
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        //根据时间排序
        queryWrapper.orderByDesc(Orders::getOrderTime);
        orderService.page(pageInfo,queryWrapper);

        List<Orders> records = pageInfo.getRecords();

        //从records中获取id
        List<Long> OrdersId = records.stream().map(Orders::getId).collect(Collectors.toList());
        List<OrderDetail> orderDetails = orderDetailService.listByOrderIds(OrdersId);

        List<OrdersDto> ordersDtoList = new ArrayList<>();

        for (Orders orders : records){
            OrdersDto ordersDto = new OrdersDto();
            ordersDto.setOrderDetails(orderDetails);
            BeanUtils.copyProperties(orders,ordersDto);
            ordersDtoList.add(ordersDto);
        }

        ordersDtoPage.setRecords(ordersDtoList);
        ordersDtoPage.setTotal(pageInfo.getTotal());

        return R.success(ordersDtoPage);
    }
}
