package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import  com.itheima.reggie.common.R;
import com.itheima.reggie.constext.BaseContext;
import  com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import  com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import  lombok.extern.slf4j.Slf4j;
import  org.springframework.beans.factory.annotation.Autowired;
import  org.springframework.web.bind.annotation.*;

import  javax.servlet.http.HttpServletRequest;
import  java.time.LocalDateTime;
import  java.util.List;
/**
 * 分类管理
 */
@RestController("adminCategoryController")
@RequestMapping("/category")
@Slf4j
public  class  CategoryController {
    @Autowired
    private  CategoryService categoryService;
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    /**
     * 新增分类
     */
    @PostMapping
    public  R<String> save(HttpServletRequest request, @RequestBody Category category){
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        Long empId = BaseContext.getCurrentId();
        category.setCreateUser(empId);
        category.setUpdateUser(empId);
        categoryService.save(category);
        return  R.success("新增分类成功");
    }

    /**
     * 查询分类
     */
    @GetMapping("/page")
    public  R<Page> page(int page, int pageSize){
        log.info("page={},pageSize={}",page,pageSize);
        //构造分页构造器
        Page<Category> pageInfo = new Page<>(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort);
        //执行查询
        categoryService.page(pageInfo,queryWrapper);
        return  R.success(pageInfo);
    }

    /**
     * 修改分类
     */
    @PutMapping
    public  R<String> update(HttpServletRequest request,@RequestBody Category category){
        log.info(category.toString());
        Long empId = (Long)request.getSession().getAttribute("employee");
        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser(empId);
        categoryService.updateById(category);
        return  R.success("修改分类成功");
    }

    /**
     * 根据id删除分类
     */
    @DeleteMapping
    public R<String> delete(Long id) {
        log.info("尝试删除分类，id为：{}", id);

        if (id == null || id <= 0) {
            return R.error("分类ID无效");
        }

        // 注入 dishService 和 setmealService（如果你还没有注入的话）
        // 参考下面添加的 service 注入部分

        // 查询是否有关联的菜品
        int dishCount = dishService.count(new LambdaQueryWrapper<Dish>().eq(Dish::getCategoryId, id));
        // 查询是否有关联的套餐（假设你有 Setmeal 实体和 SetmealService）
        int setmealCount = setmealService.count(new LambdaQueryWrapper<Setmeal>().eq(Setmeal::getCategoryId, id));

        if (dishCount > 0 || setmealCount > 0) {
            return R.error("该分类下存在菜品或套餐，无法删除");
        }

        // 没有关联数据，可以安全删除
        categoryService.removeById(id);
        return R.success("分类删除成功");
    }

    /**
     * 获取菜品分类
     */
    @GetMapping("/list")
    public R<List<Category>> getCategories(Integer type) {
        log.info("获取菜品分类列表");
        // 查询所有菜品分类
        List<Category> categories = categoryService.listByType(type);
        return R.success(categories);
    }
}
