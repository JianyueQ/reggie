package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.dto.DishDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishDtoMapper extends BaseMapper<DishDto> {
}
