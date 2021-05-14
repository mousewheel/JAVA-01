package io.kimmking.rpcfx.aspect;

import io.kimmking.rpcfx.annotation.Service;
import io.kimmking.rpcfx.client.Rpcfx;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * @Author zhurui
 * @Date 2021/3/20 5:43 下午
 * @Version 1.0
 */
@Aspect
@Component
public class ServiceAspect {

    @Before("execution(* io.kimmking.rpcfx.demo.consumer.service.*.*(..))")
    public void requestLimit(JoinPoint joinPoint) throws Exception {
        Object target = joinPoint.getTarget();
        Field[] fields = target.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            //判断当前字段是否有@Reference注解
            Service service = field.getAnnotation(Service.class);
            if (service != null) {
                //判断当前字段是否为空
                if (field.get(target) == null) {
                    //生成代理对象
                    field.set(target, Rpcfx.create(field.getType(), service.hostUrl()));
                }
            }
        }
    }

}
