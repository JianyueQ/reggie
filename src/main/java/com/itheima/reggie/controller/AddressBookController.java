package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.constext.BaseContext;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 根据用户id查询地址
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list() {
        Long currentId = BaseContext.getCurrentId();
        log.info("当前线程: {}, 用户ID: {}", Thread.currentThread().getName(), BaseContext.getCurrentId());
        List<AddressBook> list = addressBookService.listByAddressBook(currentId);
        return R.success(list);
    }

    /**
     * 设置为默认地址
     */
    @PutMapping("default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook){
        log.info("设置默认地址: {}", addressBook);
        return R.success(addressBookService.setDefault(addressBook));
    }

    /**
     * 根据地址簿id查询地址簿信息
     */
    @GetMapping("/{id}")
    public R<AddressBook> get(@PathVariable Long id){
        return R.success(addressBookService.getById(id));
    }

    /**
     * 根据地址簿id修改地址簿信息
     */
    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook){
        addressBookService.updateByAddressBook(addressBook);
        return R.success("修改成功");
    }

    /**
     * 新增地址簿信息
     */
    @PostMapping
    public R<String> save(@RequestBody AddressBook addressBook){

        addressBookService.saveAddressBook(addressBook);
        return R.success("新增成功");
    }

    /**
     * 删除地址簿信息
     */
    @DeleteMapping
    public R<String> delete(Long id){
        //逻辑删除
        addressBookService.reById(id);
        return R.success("删除成功");
    }

    /**
     * 获取默认地址
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault(){
        //查询当前登录用户的默认地址
        AddressBook addressBook = addressBookService.getOne(new LambdaQueryWrapper<AddressBook>().eq(AddressBook::getIsDefault,1).eq(AddressBook::getUserId,BaseContext.getCurrentId()));
        if(addressBook == null){
            return R.error("没有找到该对象");
        }else{
            return R.success(addressBook);
        }
    }
}
