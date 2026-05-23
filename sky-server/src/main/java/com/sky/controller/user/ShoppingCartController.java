package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/user/shoppingCart/add")
    public Result addShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shoppingCartService.addShop(shoppingCartDTO);
        return Result.success();
    }

    @GetMapping("/user/shoppingCart/list")
    public Result <List<ShoppingCart>> lookCart(){
        //这里不需要new ArrayList;因为这里是接收Service传来的数据并上传给前端的,Service的List数据会覆盖新的Array值
        //一般new List是代表需要往List里添加元素
        List<ShoppingCart> shoppingCartList = shoppingCartService.lookCart();
        return Result.success(shoppingCartList);
    }

    @DeleteMapping("/user/shoppingCart/clean")
    public Result clearCart(){
        shoppingCartService.clearCart();
        return Result.success();
    }
}
