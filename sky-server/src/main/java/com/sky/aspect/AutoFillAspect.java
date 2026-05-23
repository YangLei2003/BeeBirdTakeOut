package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面，实现公共字段自动填充功能
 */

@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    //切入点
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    //前置通知，当执行了切入点表达式“autoFillPointCut”时会先执行autoFill()
    //在通知中进行公共字段的赋值
    @Before("autoFillPointCut()")
    //这个JoinPoint是作为切入点，也就是参数，用于获取方法参数
    //可以把所拦截的方法传进joinPoint来作为方法参数
    ////这个joinPoint会拿到Employee.update(Employee)方法
    public void autoFill(JoinPoint joinPoint){
        log.info("开始进行公共字段填充");
        //获取数据库操作类型

        //这个signature是方法签名，用于获取方法名等
        //Signature接口只能粗略的获取方法信息，不能获取方法参数
        // MethodSignature接口可以获取方法参数
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//方法签名对象
        AutoFill autoFill= signature.getMethod().getAnnotation(AutoFill.class);//获取方法上的注解对象

        OperationType operationType = autoFill.value();//获取数据库操作类型

        //获取到当前被拦截的方法的参数，也就是pojo
        Object[] args=joinPoint.getArgs();
        //判断空指针
        if(args==null||args.length==0){
            return;
        }
        Object entity=args[0];//获取拦截的方法参数的第一个,做了约定,一个是pojo
        //准备赋值的数据
        LocalDateTime now= LocalDateTime.now();
        Long currentId= BaseContext.getCurrentId();

        //根据当前不同的操作类型，为对应的字段通过反射赋值
        if(operationType== OperationType.INSERT){
            try{
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);

                //通过反射为对象属性赋值
                setCreateTime.invoke(entity,now);//相当于entity.setCreateTime(now);
                setCreateUser.invoke(entity,currentId);
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(operationType== OperationType.UPDATE){
            try{
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);

                //通过反射为对象属性赋值
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
