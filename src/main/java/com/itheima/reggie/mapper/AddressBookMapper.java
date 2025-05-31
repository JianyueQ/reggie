package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.annotation.AutoFile;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
    List<AddressBook> listByAddressBook(Long currentId);

    void setDefault(AddressBook addressBook);

    void updateBatchByUserId(AddressBook addressBook);

    @AutoFile(value = OperationType.UPDATE)
    void updateByAddressBook(AddressBook addressBook);

    @AutoFile(value = OperationType.INSERT)
    void insertAddressBook(AddressBook addressBook);
}
