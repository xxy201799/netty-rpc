package com.xxy.netty.medium;

import com.alibaba.fastjson.JSONObject;
import com.xxy.netty.request.ServerRequest;
import com.xxy.netty.response.Response;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Media {
    public static Map<String, BeanMethod> beanMethodMap;
    //使用volatile保证其内存可见性以及其重排序！！主要防止其重排序
    private static volatile Media media = null;
    static {
        beanMethodMap = new HashMap<String, BeanMethod>();
    }
    private Media(){

    }

    /**
     * 单例模式，同时使用并发锁保证线程安全
     * @return
     */
    public static  Media newInstance() {
        if(media == null){
            synchronized (Media.class){
                if(media == null){
                    media = new Media();
                }
            }
        }
        return media;
    }

    /**
     * 请求来了之后进行处理
     * @param serverRequest
     * @return
     */
    public Response process(ServerRequest serverRequest) {
        Response result = null;
        try {
            String command = serverRequest.getCommand();
            BeanMethod beanMethod = beanMethodMap.get(command);
            if(beanMethod == null){
                return null;
            }
            //获取到类
            Object bean = beanMethod.getBean();
            //获取到类对应的方法
            Method method = beanMethod.getMethod();
            //获取到方法的参数
            Class paramType = method.getParameterTypes()[0];
            //获取到请求的内容
            Object content  = serverRequest.getContent();
            //将content中的值转换为paramType中的值，并返回对象
            Object args = JSONObject.parseObject(JSONObject.toJSONString(content),paramType);
            result = (Response)method.invoke(bean,args);
            result.setId(serverRequest.getId());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return result;

    }
}
