package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.entity.dto.DishDto;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishDtoService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private DishDtoService dishDtoService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 清理缓存数据
     */
    public void cleanCache(String pattern) {
        //先查询
        Set keys = redisTemplate.keys(pattern);

        redisTemplate.delete(keys);
    }


    /**
     * 查询菜品
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page={},pageSize={},name ={}",page,pageSize,name);
        //构造分页构造器
        Page<Dish> pageInfo = new Page<>(page,pageSize);

        Page<DishDto> dishDtoPage = new Page<>();
        //构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name!=null,Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行查询
        dishService.page(pageInfo,queryWrapper);

        //对象拷贝
//        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        //获取pageInfo的数据
        List<Dish> records = pageInfo.getRecords();

        //获取全部分类id
        List<Long> CategoryIds = records.stream().map(Dish::getCategoryId).collect(Collectors.toList());
        //根据ids查询分类
        List<Category> categories = categoryService.listByIds(CategoryIds);
        //按分类id进行分组
        Map<Long, String> categoryMap = categories.stream().collect(Collectors.toMap(Category::getId, Category::getName));

        List<DishDto> dishDtoList = new ArrayList<>();

        for (Dish dish : records){
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish,dishDto);
            dishDto.setCategoryName(categoryMap.get(dish.getCategoryId()));
            dishDtoList.add(dishDto);
        }

        dishDtoPage.setRecords(dishDtoList);
        dishDtoPage.setTotal(pageInfo.getTotal());


        return  R.success(dishDtoPage);
    }

    /**
     * 新增菜品
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody DishDto dishDto){
        log.info("dishDto:{}",dishDto);
        dishDto.setCreateTime(LocalDateTime.now());
        dishDto.setUpdateTime(LocalDateTime.now());
        Long empId = (Long)request.getSession().getAttribute("employee");
        dishDto.setCreateUser(empId);
        dishDto.setUpdateUser(empId);
        dishService.save(dishDto);
        //3.删除缓存
        cleanCache("dish_*");
        return R.success("新增菜品成功");
    }

    /**
     * 修改菜品
     */
    @PutMapping
    public  R<String> update(HttpServletRequest request,@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        Long empId = (Long)request.getSession().getAttribute("employee");
        dishDto.setUpdateTime(LocalDateTime.now());
        dishDto.setUpdateUser(empId);
        dishService.updateById(dishDto);
        //处理口味信息
        Long dishId = dishDto.getId();
        // 构造查询条件
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishId);
        // 删除原有关联口味
        dishFlavorService.remove(queryWrapper);
        // 如果有新的口味数据，则保存
        if (dishDto.getFlavors() != null && !dishDto.getFlavors().isEmpty()) {
            dishFlavorService.saveBatch(
                    dishDto.getFlavors().stream()
                            .peek(flavor -> flavor.setDishId(dishId)) // 设置 dishId
                            .peek(flavor -> flavor.setCreateTime(LocalDateTime.now()))
                            .peek(flavor -> flavor.setUpdateTime(LocalDateTime.now()))
                            .peek(flavor -> flavor.setCreateUser(empId))
                            .peek(flavor -> flavor.setUpdateUser(empId))
                            .collect(Collectors.toList())
            );
        }
        //3.删除缓存
        cleanCache("dish_*");
        return  R.success("修改成功");
    }

    /**
     * 根据菜品id查询菜品信息
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        Dish dish = dishService.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return R.success(dishDto);
    }

    /**
     * 批量起售或者停售也可以单个起售或者停售菜品管理
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Integer status, @RequestParam List<Long> ids) {
        log.info("修改菜品状态：status={}, ids={}", status, ids);

        if (ids == null || ids.isEmpty()) {
            return R.error("请选择要修改的菜品");
        }

        // 查询所有要修改的菜品
        List<Dish> dishes = dishService.listByIds(ids);

        // 修改每条记录的状态
        dishes.forEach(dish -> dish.setStatus(status));

        // 批量更新
        dishService.updateBatchById(dishes);

        //3.删除缓存
        cleanCache("dish_*");

        return R.success("修改菜品状态成功");
    }

    /**
     * 批量删除或单个删除菜品
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info("删除菜品及其口味，ids = {}", ids);

        if (ids == null || ids.isEmpty()) {
            return R.error("请选择要删除的菜品");
        }
        // 1. 查询所有待删除的菜品
        List<Dish> dishes = dishService.listByIds(ids);
        // 2. 检查是否有起售中的菜品
        boolean hasOnSaleDish = dishes.stream().anyMatch(dish -> dish.getStatus() == 1);
        if (hasOnSaleDish) {
            return R.error("起售中的菜品不允许删除，请先停售后再操作");
        }
        // 1. 先删除口味表中的数据
        LambdaQueryWrapper<DishFlavor> flavorQueryWrapper = new LambdaQueryWrapper<>();
        flavorQueryWrapper.in(DishFlavor::getDishId, ids);
        dishFlavorService.remove(flavorQueryWrapper);

        // 2. 再删除菜品数据
        dishService.removeByIds(ids);

        //3.删除缓存
        cleanCache("dish_*");

        return R.success("删除菜品及口味成功");
    }

    /**
     * 根据分类id查询菜品
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        //设置redis缓存的key
        String key = "dish_" + dish.getCategoryId();
        log.info("查询菜品列表，redis缓存的key：{}", key);
        //查询redis缓存
        List<DishDto> list = (List<DishDto>) redisTemplate.opsForValue().get(key);

        if (list != null) {
            return R.success(list);
        }
        list = dishService.list(dish);
        //将数据保存到redis缓存中
        redisTemplate.opsForValue().set(key, list);
        return R.success(list);
    }


}
