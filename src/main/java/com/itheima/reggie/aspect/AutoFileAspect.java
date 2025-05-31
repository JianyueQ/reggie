package com.itheima.reggie.aspect;

import com.itheima.reggie.annotation.AutoFile;
import com.itheima.reggie.constant.AutoFileConstant;
import com.itheima.reggie.constext.BaseContext;
import com.itheima.reggie.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Slf4j
@Component
public class AutoFileAspect {

    /**
     * 切入点
     */
    @Pointcut("execution(* com.itheima.reggie.mapper.*.*(..)) && @annotation(com.itheima.reggie.annotation.AutoFile)")
    public void autoFilePointCut(){}

    /**
     * 前置通知
     */
    @Before("autoFilePointCut()")
    public void autoFile(JoinPoint joinPoint){
        log.info("自动填充开始...");

//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//获取方法签名对象
//        AutoFile autoFile = signature.getClass().getAnnotation(AutoFile.class);//获取方法上的注解
        // 获取被拦截的方法对象
        Method method = ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getMethod();
        // 获取方法上的注解
        AutoFile autoFile = method.getAnnotation(AutoFile.class);
        OperationType operationType = autoFile.value();  //获取注解中的数据库操作类型

        //获取被拦截方法的参数  实体对象
        Object[] args = joinPoint.getArgs();
        if(args == null || args.length == 0){
            return;
        }
        Object entity = args[0];

        //准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        //TODO 获取当前登录用户id
        Long currentId = BaseContext.getCurrentId();

        if(operationType == OperationType.INSERT){
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFileConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFileConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFileConstant.SET_CREATE_USER, Long.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFileConstant.SET_UPDATE_USER, Long.class);

                //通过反射为对象赋值
                setCreateTime.invoke(entity,now);
                setUpdateTime.invoke(entity,now);
                setCreateUser.invoke(entity,currentId);
                setUpdateUser.invoke(entity,currentId);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }

        }else if (operationType == OperationType.UPDATE){
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFileConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFileConstant.SET_UPDATE_USER, Long.class);

                //通过反射为对象赋值
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
