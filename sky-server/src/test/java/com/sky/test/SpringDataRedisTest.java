package com.sky.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

//@SpringBootTest
public class SpringDataRedisTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testRedisTemplate(){
        System.out.println(redisTemplate);
        //这五个是RedisTemplate的封装的五种操作类型的方法
        redisTemplate.opsForValue();//操作values值
        redisTemplate.opsForHash();//操作hash值
        redisTemplate.opsForList();//操作list列表值
        redisTemplate.opsForSet();//操作set无序集合值
        redisTemplate.opsForZSet();//操作Zset有序集合值
    }

    @Test
    public void testString(){
        //set get setex setnx (setex:设置的同时设置TTL值,setnx:如果key不存在就插入，存在就不动)
        redisTemplate.opsForValue().set("city","北京");
        //到这一步为止，value还是乱码，但是key不是乱码， 是因为在配置类中给redisTemplate设置了key的序列化器
        String city = (String) redisTemplate.opsForValue().get("city");//记住，是获取value值
        System.out.println(city);

        redisTemplate.opsForValue().set("code","1234",3, TimeUnit.MINUTES);
        //在Java的Redis中，无论是key还是value都是对象，所以，在存储的时候，需要将数据进行序列化
        redisTemplate.opsForValue().setIfAbsent("lock","1");
        redisTemplate.opsForValue().setIfAbsent("lock","2");//这个不成功，这个已存在是看key的，记住
    }

    @Test
    public void testHash(){
        //hset hget hdel hkeys hvals
        HashOperations hashOperations = redisTemplate.opsForHash();
        hashOperations.put("100","name","tom");
        hashOperations.put("100","age",18);
        String name = (String) hashOperations.get("100", "name");
        System.out.println(name);
        Set keys = hashOperations.keys("100");
        System.out.println(keys);
        List values = hashOperations.values("100");
        System.out.println (values);
        hashOperations.delete("100","age");
    }
}
