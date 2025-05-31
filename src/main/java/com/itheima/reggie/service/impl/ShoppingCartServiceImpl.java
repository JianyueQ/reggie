package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.mapper.ShoppingCartMapper;
import com.itheima.reggie.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public ShoppingCart add(ShoppingCart shoppingCart) {
        //根据用户id和菜品id或者套餐id查询购物车
        ShoppingCart list = shoppingCartMapper.queryByCondition(shoppingCart);
        //如果购物车不为空则将该商品加1
        if (list != null) {
            list.setNumber(list.getNumber() + 1);
            shoppingCartMapper.updateShoppingCart(list);
        }else {
            //购物车为空则进行添加数据
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insertShoppingCart(shoppingCart);
            list = shoppingCart;
        }
        return list;
    }

    @Override
    public void clean(Long userId) {
        //清空购物车
        shoppingCartMapper.clean(userId);
    }

    @Override
    public ShoppingCart sub(ShoppingCart shoppingCart) {
        //根据用户id和菜品id或者套餐id查询购物车
        ShoppingCart list = shoppingCartMapper.queryByCondition(shoppingCart);
        //如果购物车不为空则将该商品减1
        if (list != null){
            if (list.getNumber() > 1){
                list.setNumber(list.getNumber() - 1);
                shoppingCartMapper.updateShoppingCart(list);
            }
        }
        return list;
    }

    @Override
    public List<ShoppingCart> listByUserId(Long currentId) {
        return shoppingCartMapper.selectByUserId(currentId);
    }
}
