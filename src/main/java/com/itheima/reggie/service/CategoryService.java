package com.itheima.reggie.service;

import  com.baomidou.mybatisplus.extension.service.IService;
import  com.itheima.reggie.entity.Category;

import java.util.List;

public  interface  CategoryService extends  IService<Category> {

    /**
     * 根据类型查询
     * @param type
     * @return
     */
    List<Category> listByType(Integer type);
}