package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;

    //微信服务接口地址
    public static final String WeChat_Login="https://api.weixin.qq.com/sns/jscode2session";
    public User weChatLogin(UserLoginDTO userLoginDTO) {
        //调用微信接口服务，获得当前微信用户的openid
        Map<String, String> weChatMap=new HashMap<>();
        weChatMap.put("appid",weChatProperties.getAppid());
        weChatMap.put("secret",weChatProperties.getSecret());
        weChatMap.put("js_code",userLoginDTO.getCode());
        weChatMap.put("grant_type","authorization_code");
        //这里返回的是json格式的字符串，需要转换为对象再进行操作
        String json=HttpClientUtil.doGet(WeChat_Login,weChatMap);

        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");

        //判断openid是否为空
        if(openid==null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        //判断是否为新用户
        User user= userMapper.getByOpenid(openid);

        //若为新用户，自动完成注册
        if(user==null){
            user=User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }
        //返回这个用户的对象
        return user;
    }
}
