package com.sky.controller.user;

import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class addressBookController {
    @Autowired
    private AddressBookService addressService;

    /**
     * 新增地址
     * @param addressBook
     * @return
     */
    @PostMapping("/user/addressBook")
    public Result addAddress(@RequestBody AddressBook addressBook){
        addressService.add(addressBook);
        return Result.success();
    }

    /**
     * 查询当前登录用户的所有地址信息
     * @return
     */
    @GetMapping("/user/addressBook/list")
    public Result<List<AddressBook>> list(){
        List<AddressBook> addressList= addressService.list();
        return Result.success(addressList);
    }

    /**
     * 查询默认地址
     */
    @GetMapping("/user/addressBook/default")
    public Result<List<AddressBook>> getDefault(){
        List<AddressBook> addressBookList = addressService.getDefault();
        return Result.success(addressBookList);
    }

    /**
     * 根据ID修改地址
     */
    @PutMapping("/user/addressBook")
    public Result update(@RequestBody AddressBook addressBook){
        addressService.update(addressBook);
        return Result.success();
    }

    /**
     * 根据ID删除地址
     */
    @DeleteMapping("/user/addressBook")
    public Result delete(Long id){
        addressService.deleteById(id);
        return Result.success();
    }

    /**
     * 根据ID查询地址
     */

    @GetMapping("/user/addressBook/{id}")
    public Result<AddressBook> getById(@PathVariable Long id){
        return Result.success(addressService.getById(id));
    }

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/user/addressBook/default")
    public Result setDefault(@RequestBody AddressBook addressBook){
        addressService.setDefault(addressBook);
        return Result.success();
    }





}
