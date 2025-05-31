package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.constext.BaseContext;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.mapper.AddressBookMapper;
import com.itheima.reggie.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Override
    public List<AddressBook> listByAddressBook(Long currentId) {

        return addressBookMapper.listByAddressBook(currentId);
    }

    @Override
    public AddressBook setDefault(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(0);
        //只能有一个默认地址,然后设置为否
        addressBookMapper.updateBatchByUserId(addressBook);
        //在根据地址id设置为默认地址
        addressBook.setIsDefault(1);
        addressBookMapper.setDefault(addressBook);
        return addressBook;
    }

    @Override
    public void updateByAddressBook(AddressBook addressBook) {
        addressBookMapper.updateByAddressBook(addressBook);
    }

    @Override
    public void reById(Long id) {
        AddressBook addressBook = new AddressBook();
        addressBook.setId(id);
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDeleted(1);
        addressBookMapper.updateByAddressBook(addressBook);
    }

    @Override
    public void saveAddressBook(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDeleted(0);
        addressBookMapper.insertAddressBook(addressBook);
    }
}
