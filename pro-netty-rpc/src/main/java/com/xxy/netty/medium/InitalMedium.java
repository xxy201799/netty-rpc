package com.xxy.netty.medium;

import com.xxy.netty.annotation.Remote;
import com.xxy.netty.annotation.RemoteInvoke;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 中介者模式，处理业务代码
 * 该类继承了BeanPostProcessor
 * 实现了bean初始化前后的处理
 */
@Component
public class InitalMedium implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //如果有controller注解，进行处理
        if(bean.getClass().isAnnotationPresent(Remote.class)){
            //存储其中的方法
            Method[] methods = bean.getClass().getMethods();
            for (Method m:methods) {
                //保存类的全名以及方法，放入hashmap
                String key = bean.getClass().getInterfaces()[0].getName() + "." + m.getName();
                Map<String,BeanMethod> beanMethodMap = Media.beanMethodMap;
                BeanMethod beanMethod = new BeanMethod();
                beanMethod.setBean(bean);
                beanMethod.setMethod(m);
                beanMethodMap.put(key,beanMethod);
            }
        }
        return bean;
    }
}
