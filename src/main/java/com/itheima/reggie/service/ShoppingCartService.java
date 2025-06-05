package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService extends IService<ShoppingCart> {
    ShoppingCart add(ShoppingCart shoppingCart);

    void clean(Long userId);

    ShoppingCart sub(ShoppingCart shoppingCart);

    List<ShoppingCart> listByUserId(Long currentId);

    void addAgain(Orders again);
}
