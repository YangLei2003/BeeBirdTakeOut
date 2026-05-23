package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;


    @PostMapping("/admin/dish")
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO){
        dishService.saveWithFlavor(dishDTO);
        String key="dish_"+dishDTO.getCategoryId();
        redisTemplate.delete(key);

        return Result.success();
    }

    @GetMapping("/admin/dish/page")
    @ApiOperation("菜品分页查询")
    //这里不用加@RequestBody，因为接口文档的请求参数的Query，不是json
    //具体来说是在网址后面加?key=value
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        PageResult pageResult= dishService.dishPageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping("/admin/dish")
    @ApiOperation("批量删除菜品")
    public Result delete(@RequestParam List<Long> ids){
        dishService.deleteBatch(ids);
        RedisDelete("dish_*");
        return Result.success();
    }

    /**
     * 根据菜品ID查询菜品
     */

    @GetMapping("/admin/dish/{id}")
    @ApiOperation("根据ID查询菜品")
    public Result<DishVO> getById(@PathVariable Long id){
        DishVO dishVo = dishService.getById(id);
        return Result.success(dishVo);
    }

    @PutMapping("admin/dish")
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO){
        dishService.updateWithFlavor(dishDTO);
        RedisDelete("dish_*");
        return Result.success();
    }

    @PostMapping("admin/dish/status/{status}")
    @ApiOperation("菜品起售/停售")
    public Result startOrStop(@PathVariable Integer status,Long id){
        dishService.startOrStop(status,id);
        return Result.success();

    }



    private void RedisDelete(String mode){
        Set keys=redisTemplate.keys(mode);
        redisTemplate.delete(keys);
    }

}
