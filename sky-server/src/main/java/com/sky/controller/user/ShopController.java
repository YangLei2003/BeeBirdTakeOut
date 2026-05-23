package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController("userRestController")// 这个注解是SpringMVC的，用于标识这个类是一个控制器,因为和admin有同名的接口,所以必须要这样将bean加以区分,否则会报错
@Api(tags = "店铺相关接口")

public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;

    static private String KEY="SHOP_STATUS";
    @GetMapping("/user/shop/status")
    public Result<Integer> getShopStatus(){
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        log.info("获取到店铺的营业状态为:{}",status==1?"营业中":"打烊了喵");
        return Result.success(status);
    }


}
