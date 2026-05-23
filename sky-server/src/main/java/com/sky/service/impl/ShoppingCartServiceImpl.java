package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    public void addShop(ShoppingCartDTO shoppingCartDTO) {
        //如果同一个商品点了两份，应该在number字段加1而非新加入数据
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        //这里应该先根据id查询购物车数据库
        List<ShoppingCart> shoppingCarts=shoppingCartMapper.getById(shoppingCart);
        //判断购物车数据库已经有数据
        if(shoppingCarts!=null && shoppingCarts.size()>0){
            ShoppingCart cart = shoppingCarts.get(0);//这里虽然是用List接收，但是实际上只会查出一个数据，因为明确传入了菜品id或者套餐id
            //这里，如果用户加入购物车，此时会在数据库加入一条数据(这个业务代码在后面实现)
            //这个if是如果数据库里已经有数据，即size>0,就代表购物车里已经有数据了
            //所以如果进入这个if判断就代表用户点+1了,只需要number+1就可以了
            cart.setNumber(cart.getNumber()+1);
            shoppingCartMapper.numberAdd(cart);
        }else{
            //这段是为菜品或套餐添加必要字段内容,不可能同时菜品和套餐,因为这是单次点菜
            if(shoppingCart.getDishId()!=null){//代表用户此次点的是菜品
                Dish dish=dishMapper.getById(shoppingCart.getDishId());
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setNumber(1);
                shoppingCart.setDishFlavor(shoppingCartDTO.getDishFlavor());

            }else{
                //not null非空字段如果是空的话就不能插入数据库,插不进去
                Setmeal setmeal=setmealMapper.getById(shoppingCart.getSetmealId());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setNumber(1);
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.addDish(shoppingCart);
        }
    }

    public List<ShoppingCart> lookCart() {
        Long userId=BaseContext.getCurrentId();
        ShoppingCart shoppingCart=new ShoppingCart();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> shoppingCartList= shoppingCartMapper.getByUserId(shoppingCart);
        return shoppingCartList;
    }

    public void clearCart(){
        Long userId=BaseContext.getCurrentId();
        shoppingCartMapper.clear(userId);
    }
}
