package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.AddressBookBusinessException;
import com.itheima.reggie.constext.BaseContext;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.entity.dto.OrdersDto;
import com.itheima.reggie.mapper.OrderMapper;
import com.itheima.reggie.mapper.ShoppingCartMapper;
import com.itheima.reggie.service.OrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private AddressBookServiceImpl addressBookService;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private OrderDetailServiceImpl orderDetailService;
    @Autowired
    private ShoppingCartServiceImpl shoppingCartService;

    @Override
    public void update(Orders orders) {
        orderMapper.update(orders);
    }

    @Override
    public void submit(Orders orders) {

        OrdersDto ordersDto = new OrdersDto();
        BeanUtils.copyProperties(orders, ordersDto);
        //根据地址簿id查询地址
        AddressBook addressBook = addressBookService.getById(ordersDto.getAddressBookId());
        //处理业务异常,地址簿为空
        if (addressBook == null) {
            throw new AddressBookBusinessException("地址簿为空");
        }
        ordersDto.setAddress(addressBook.getDetail());
        ordersDto.setPhone(addressBook.getPhone());
        ordersDto.setConsignee(addressBook.getConsignee());
        //查询用户名称
        User user = userService.getById(BaseContext.getCurrentId());
        ordersDto.setUserName(user.getName());
        ordersDto.setUserId(BaseContext.getCurrentId());
        //根据用户id查询购物车
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectByUserId(BaseContext.getCurrentId());
        //处理业务异常,购物车为空
        if (shoppingCarts == null || shoppingCarts.size() == 0) {
            throw new AddressBookBusinessException("购物车为空");
        }
        //计算实际收取的总金额
        BigDecimal decimal = shoppingCarts.stream().map(shoppingCart ->
                shoppingCart.getAmount().multiply(new BigDecimal(shoppingCart.getNumber()))).reduce(BigDecimal::add).get();
        ordersDto.setAmount(decimal);
        //设置下单时间
        ordersDto.setOrderTime(LocalDateTime.now());
        //设置结账时间
        ordersDto.setCheckoutTime(LocalDateTime.now());
        //生成订单号
        ordersDto.setNumber(String.valueOf(System.currentTimeMillis()));
        //设置订单状态
        ordersDto.setStatus(2);
        //调用mapper
        orderMapper.insertOrdersDto(ordersDto);

        //向购物车详细表插入数据
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (ShoppingCart cart : shoppingCarts){
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart,orderDetail);
            orderDetail.setOrderId(ordersDto.getId());
            orderDetails.add(orderDetail);
        }
        orderDetailService.saveBatch(orderDetails);
        //清空购物车
        shoppingCartService.clean(BaseContext.getCurrentId());
    }
}
