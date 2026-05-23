package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    //这里要查询ID+dishID或者套餐id，刚刚在思考如果只点了菜品没点套餐，前端传入的套餐ID是null会导致查询出问题
    //所以这里需要用XML动态拼接，如果套餐ID是null就省略
    List<ShoppingCart> getById(ShoppingCart shoppingCart);

    @Update("update shopping_cart set number=#{number} where user_id=#{userId} and dish_id=#{dishId}")
    void numberAdd(ShoppingCart cart);


    @Insert("insert into shopping_cart (name,user_id,dish_id,setmeal_id,dish_flavor,number,amount,image,create_time)" +
            " values " +
            "(#{name},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{number},#{amount},#{image},#{createTime})")
    void addDish(ShoppingCart shoppingCart);

    @Select("select * from shopping_cart where user_id=#{userId}")
    List<ShoppingCart> getByUserId(ShoppingCart shoppingCart);

    @Delete("delete from shopping_cart where user_id=#{userId}")
    void clear(Long userId);

    @Select("select * from shopping_cart where user_id=#{userId}")
    List<ShoppingCart> getByID(Long userId);
}
