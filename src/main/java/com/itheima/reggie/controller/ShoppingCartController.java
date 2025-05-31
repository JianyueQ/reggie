package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.constext.BaseContext;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 获取购物车列表
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        return R.success(shoppingCartService.listByUserId(BaseContext.getCurrentId()));
    }

    /**
     * 向购物车添加商品
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart, HttpSession session){
        //从session中获取用户id
        shoppingCart.setUserId((Long) session.getAttribute("user"));
        log.info("购物车数据：{}", shoppingCart);
        ShoppingCart shoppingCartOne = shoppingCartService.add(shoppingCart);
        return R.success(shoppingCartOne);
        //查询当前菜品是否在购物车中
//        Long dishId = shoppingCart.getDishId();
//        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(ShoppingCart::getUserId, shoppingCart.getUserId());
//        if(dishId != null){
//            queryWrapper.eq(ShoppingCart::getDishId, dishId);
//        }else {
//            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
//        }
//        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);
//        if (cartServiceOne != null) {
//            cartServiceOne.setNumber(cartServiceOne.getNumber() + 1);
//            shoppingCartService.updateById(cartServiceOne);
//        }else {
//            shoppingCart.setNumber(1);
//            shoppingCart.setCreateTime(LocalDateTime.now());
//            shoppingCartService.save(shoppingCart);
//            cartServiceOne = shoppingCart;
//        }
//        return R.success(cartServiceOne);
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clean")
    public R<String> clean(HttpSession session) {
        Long userId = (Long) session.getAttribute("user");
        shoppingCartService.clean(userId);
        return R.success("清空购物车成功");
    }

    /**
     * 减少购物车商品
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart, HttpSession session) {
        Long userId = (Long) session.getAttribute("user");
        shoppingCart.setUserId(userId);
        ShoppingCart shoppingCartOne = shoppingCartService.sub(shoppingCart);
        return R.success(shoppingCartOne);
    }
}
