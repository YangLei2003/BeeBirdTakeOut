package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {

   void add(AddressBook addressBook);

   List<AddressBook> list();

   void setDefault(AddressBook addressBook);

   List<AddressBook> getDefault();

   void update(AddressBook addressBook);

   AddressBook getById(Long id);

   void deleteById(Long id);
}
