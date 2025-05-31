package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.dto.DishDto;
import com.itheima.reggie.mapper.DishDtoMapper;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishDtoService;
import org.springframework.stereotype.Service;

@Service
public class DishDtoServiceImpl extends ServiceImpl<DishDtoMapper, DishDto> implements DishDtoService {
}
