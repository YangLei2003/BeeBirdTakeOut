package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//标识这个自定义注解只能加在方法上
@Target(ElementType.METHOD)
//固定写法
@Retention(RetentionPolicy.RUNTIME)
//自定义注解，用于标识需要自动填充的注解
public @interface AutoFill {
    //OperationType类的枚举内容，用于指定填充数据的操作类型
    //UPDATE INSERT
    OperationType value();
}
