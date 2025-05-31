package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper extends BaseMapper<SetmealDish> {

    void insertSetmealDish(List<SetmealDish> setmealDishes);

    List<SetmealDish> selectList(Long id);

    void deleteBySetmealId(Long id);

    List<SetmealDish> selectByIds(List<Long> ids);

}
