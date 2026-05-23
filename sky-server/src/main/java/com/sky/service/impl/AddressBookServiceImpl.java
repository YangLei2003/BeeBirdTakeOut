package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {
    @Autowired
    private AddressBookMapper addressBookMapper;
    public void add(AddressBook addressBook) {
        Long userId= BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        addressBook.setIsDefault(1);
        addressBookMapper.add(addressBook);
    }

    public List<AddressBook> list() {
        AddressBook addressBook=new AddressBook();
        Long userId=BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        List<AddressBook> addressList=addressBookMapper.addressList(addressBook);
        return addressList;
    }

    public void setDefault(AddressBook addressBook) {
        Long userId=BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        addressBookMapper.clareDefault(addressBook);
        addressBookMapper.setDefault(addressBook);
    }

    public List<AddressBook> getDefault() {
        AddressBook addressBook=new AddressBook();
        Long userId=BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        List<AddressBook>addressBookList= addressBookMapper.getDefault(addressBook);
        return  addressBookList;
    }

    public void update(AddressBook addressBook) {
        addressBook.setIsDefault(0);
        addressBookMapper.update(addressBook);
    }

    public AddressBook getById(Long id) {
        return addressBookMapper.getById(id);

    }

    public void deleteById(Long id) {
        addressBookMapper.deleteById(id);
    }
}
