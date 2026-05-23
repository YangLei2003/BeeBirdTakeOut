package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("adminRestController")
@Api(tags = "店铺相关接口")
@Slf4j

public class ShopController {
    static private final String KEY="SHOP_STATUS";
    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation("设置营业状态")
    @PutMapping("/admin/shop/{status}")
    public Result setShopStatus(@PathVariable Integer status){

        log.info("设置营业状态:{}",status==1?"营业中":"打样了喵");
        redisTemplate.opsForValue().set(KEY,status);
        return Result.success();
    }

    @ApiOperation("查询营业状态")
    @GetMapping("/admin/shop/status")
    public Result<Integer> getShopStatus(){
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        log.info("获取到店铺的营业状态为:{}",status==1?"营业中":"打烊了喵");
        return Result.success(status);
    }
}
