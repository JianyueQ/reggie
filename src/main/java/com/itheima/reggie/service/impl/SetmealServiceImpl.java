package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.entity.dto.DishDto;
import com.itheima.reggie.entity.dto.SetmealDto;
import com.itheima.reggie.mapper.SetmealDishMapper;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDto,setmeal);
        setmealMapper.insertSetmeal(setmeal);
        List<SetmealDish> list = setmealDto.getSetmealDishes();
        List<SetmealDish> setmealDishes = new ArrayList<>();
        for (SetmealDish item : list){
            SetmealDish setmealDish = new SetmealDish();
            BeanUtils.copyProperties(item,setmealDish);
            setmealDish.setSetmealId(setmeal.getId());
            setmealDish.setCreateTime(LocalDateTime.now());
            setmealDish.setUpdateTime(LocalDateTime.now());
            setmealDish.setCreateUser(1L);
            setmealDish.setUpdateUser(1L);
            setmealDishes.add(setmealDish);
        }
        setmealDishMapper.insertSetmealDish(setmealDishes);
    }

    /**
     * 根据id查询套餐和菜品
     * @param id
     * @return
     */
    @Override
    public SetmealDto getByIdWithDish(Long id) {
        Setmeal setmeal = setmealMapper.selectById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
        List<SetmealDish> setmealDishes = setmealDishMapper.selectList(setmeal.getId());
        setmealDto.setSetmealDishes(setmealDishes);
        return setmealDto;
    }

    @Override
    public void updateBySetmealDto(SetmealDto setmealDto) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDto,setmeal);
        setmealMapper.update(setmeal);
        setmealDishMapper.deleteBySetmealId(setmealDto.getId());
        List<SetmealDish> list = setmealDto.getSetmealDishes();
        List<SetmealDish> setmealDishes = new ArrayList<>();
        for (SetmealDish item : list){
            SetmealDish setmealDish = new SetmealDish();
            BeanUtils.copyProperties(item,setmealDish);
            setmealDish.setSetmealId(setmeal.getId());
            setmealDish.setCreateTime(LocalDateTime.now());
            setmealDish.setUpdateTime(LocalDateTime.now());
            setmealDish.setCreateUser(setmealDto.getUpdateUser());
            setmealDish.setUpdateUser(setmealDto.getUpdateUser());
            setmealDishes.add(setmealDish);
        }
        setmealDishMapper.insertSetmealDish(setmealDishes);
    }

    @Override
    public void deleteBySetmealDishIds(List<Long> ids) {
        List<SetmealDish> setmealDishes = setmealDishMapper.selectByIds(ids);
        for (SetmealDish item : setmealDishes){
            if (item == null){
                return;
            }else {
                item.setIsDeleted(1);
                setmealDishMapper.updateById(item);
            }
        }
    }

    @Override
    public List<DishDto> listById(Long id) {
        return setmealMapper.listById(id);
    }


}
