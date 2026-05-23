package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SetmealController {
    @Autowired
    private SetmealService setmealservice;

    @GetMapping("/user/setmeal/list")
    @Cacheable(cacheNames="setmealCache",key="#categoryId")//Key为：setmealCache::100
    public Result<List<Setmeal>> getCategoryById(Long categoryId){
        //还是用这种方法，如果直接非常粗暴的传入Service然后调SQL，头脑简单的会导致status不工作
        Setmeal setmeal=new Setmeal();
        setmeal.setCategoryId(categoryId);
        //还有就是在Controller层就设置Status可以防止把未上架的传出去，类似密码的******
        setmeal.setStatus(StatusConstant.ENABLE);
        List<Setmeal> setmealList=setmealservice.getCategoryById(setmeal);
        return Result.success(setmealList);
    }

    @GetMapping("/user/setmeal/dish/{id}")
    public Result<List<DishItemVO>> getDishBySetmealId(@PathVariable Long id){
        List<DishItemVO> DishItemVOList=setmealservice.getDishBySetmealId(id);
        return Result.success(DishItemVOList);
    }
}
