package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.dto.DishDto;
import com.itheima.reggie.entity.dto.SetmealDto;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DishService dishService;

    /**
     * 删除缓存
     */
    private void cleanCache(String key){
        //先查询
         Set keys = redisTemplate.keys(key);
         //删除
         redisTemplate.delete(keys);

    }

    /**
     * 查询套餐管理列表
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        //构造分页构造器
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        //构造条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null,Setmeal::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //执行查询
        setmealService.page(pageInfo, queryWrapper);

        List<Setmeal> records = pageInfo.getRecords();

        List<Long> CategoryIds = records.stream().map(Setmeal::getCategoryId).collect(Collectors.toList());
        List<Category> categories = categoryService.listByIds(CategoryIds);

        Map<Long, String> categoryMap = categories.stream().collect(Collectors.toMap(Category::getId, Category::getName));

        List<SetmealDto> setmealDtoList = records.stream().map(item -> {
            SetmealDto setmealDto = new SetmealDto();
            setmealDto.setCategoryName(categoryMap.get(item.getCategoryId()));
            BeanUtils.copyProperties(item,setmealDto);
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(setmealDtoList);
        dtoPage.setTotal(pageInfo.getTotal());

        return R.success(dtoPage);
    }

    /**
     * 批量更改套餐状态或更改单个套餐状态
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status ,@RequestParam List<Long> ids){

        List<Setmeal> setmeals = setmealService.listByIds(ids);

        setmeals.forEach(setmeal -> setmeal.setStatus(status));

        setmealService.updateBatchById(setmeals);

        cleanCache("setmealCache:*");

        return R.success("修改套餐状态成功");
    }

    /**
     * 新增套餐
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("套餐信息：{}",setmealDto);
        setmealService.saveWithDish(setmealDto);
        cleanCache("setmealCache:*");
        return R.success("新增套餐成功");
    }

    /**
     * 根据套餐id查询套餐信息
     */
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    /**
     * 修改套餐
     */
    @PutMapping
    public  R<String> update(HttpServletRequest request, @RequestBody SetmealDto setmealDto){
        log.info(setmealDto.toString());
        Long empId = (Long)request.getSession().getAttribute("employee");
        setmealDto.setUpdateUser(empId);
        setmealService.updateBySetmealDto(setmealDto);
        cleanCache("setmealCache:*");
        return  R.success("修改成功");
    }

    /**
     * 批量删除套餐或者单个删除套餐
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("ids:{}",ids);
        //查询是否为停售状态status = 0,如果status = 1则无法删除
        List<Setmeal> setmealList = setmealService.listByIds(ids);
        for (Setmeal setmeal : setmealList){
            if (setmeal.getStatus() == 1){
                return R.error("套餐正在售卖中，无法删除");
            }
        }
        //逻辑删除
        setmealService.removeByIds(ids);
        //对应的菜品也应该逻辑删除
        setmealService.deleteBySetmealDishIds(ids);

        cleanCache("setmealCache:*");
        return R.error("已删除");
    }

    /**
     *根据分类id和状态查询套餐
     */
    @GetMapping("/list")
//    @Cacheable(cacheNames =  "setmealCache",key = "'setmealCache:' + #setmeal.categoryId + '_' + #setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal) {

        String keys = "setmeal_" + setmeal.getCategoryId() + "_" + setmeal.getStatus();
        log.info("keys:{}",keys);
        //查询缓存
        List<Setmeal> list = (List<Setmeal>) redisTemplate.opsForValue().get(keys);
        if ( list != null){
            return R.success(list);
        }
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        list = setmealService.list(queryWrapper);

        // 缓存数据
        redisTemplate.opsForValue().set(keys,list);
        return R.success(list);
    }

    /**
     *  获取套餐详情
     */
    @GetMapping("/dish/{id}")
     public R<List<DishDto>> getDish(@PathVariable Long id){
         List<DishDto> dishDtoList = setmealService.listById(id);
         return R.success(dishDtoList);
    }

}
