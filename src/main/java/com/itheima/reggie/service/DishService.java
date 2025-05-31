package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.dto.DishDto;

import java.util.List;

public interface DishService extends IService<Dish> {
    List<Dish> listByCategoryId(Long categoryId);

    List<DishDto> list(Dish dish);
}
