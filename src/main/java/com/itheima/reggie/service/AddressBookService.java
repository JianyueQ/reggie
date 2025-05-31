package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.AddressBook;

import java.util.List;

public interface AddressBookService extends IService<AddressBook> {
    List<AddressBook> listByAddressBook(Long currentId);

    AddressBook setDefault(AddressBook addressBook);

    void updateByAddressBook(AddressBook addressBook);

    void reById(Long id);

    void saveAddressBook(AddressBook addressBook);
}
