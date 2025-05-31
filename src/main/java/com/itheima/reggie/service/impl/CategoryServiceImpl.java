package com.itheima.reggie.service.impl;

import  com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import  com.itheima.reggie.entity.Category;
import  com.itheima.reggie.mapper.CategoryMapper;
import  com.itheima.reggie.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import  org.springframework.stereotype.Service;

import java.util.List;

@Service
public  class  CategoryServiceImpl extends  ServiceImpl<CategoryMapper,Category>
        implements  CategoryService{

    @Autowired
    private  CategoryMapper categoryMapper;

    /**
     * 根据类型查询
     * @param type
     * @return
     */
    @Override
    public List<Category> listByType(Integer type) {
        return categoryMapper.selectListByType(type);
    }
}