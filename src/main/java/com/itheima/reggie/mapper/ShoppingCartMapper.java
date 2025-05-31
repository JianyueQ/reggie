package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {

    ShoppingCart add(ShoppingCart shoppingCart);

    ShoppingCart queryByCondition(ShoppingCart shoppingCart);

    void insertShoppingCart(ShoppingCart shoppingCart);

    void updateShoppingCart(ShoppingCart shoppingCart);

    void clean(Long userId);

    void removeById(Long id, Long userId);

    List<ShoppingCart> selectByUserId(Long currentId);
}
