package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userDishController")
@Api("根据分类id查询菜品")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/user/dish/list")
    public Result<List<DishVO>> getDishById(Long categoryId){
        //构造redis中的key，规则：dish_分类ID
        String key="dish_"+categoryId;
        //查询redis中是否存在菜品数据
            //放进去的是什么类型的对象,取出来就是什么类型的对象
        List<DishVO> list = (List<DishVO>) redisTemplate.opsForValue().get(key);
        if(list!=null && list.size()>0){
            //如果存在，直接返回，不用查数据库
            return Result.success(list);
        }

        Dish dish=new Dish();
            //懒狗写法，调用现成的pojo，然后随便设置一个值
            //然后有了值就可以通过xml文件来写具体的SQL，因为不赋值的null值，就会忽略
            //这样就可以不用再写一个新的DTO
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);

        list=dishService.getDishByCategoryId(dish);
        //如果不存在，就查询数据库，将查到的数据存入redis中
        redisTemplate.opsForValue().set(key,list);

        return Result.success(list);
    }
}
