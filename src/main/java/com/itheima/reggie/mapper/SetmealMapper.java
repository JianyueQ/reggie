package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.annotation.AutoFile;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.dto.DishDto;
import com.itheima.reggie.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealMapper extends BaseMapper<Setmeal> {
    @AutoFile(OperationType.INSERT)
    void insertSetmeal(Setmeal setmeal);

    void update(Setmeal setmeal);

    List<DishDto> listById(Long id);
}
