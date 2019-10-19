package com.xxy.service;

import com.alibaba.fastjson.JSONObject;
import com.xxy.netty.annotation.RemoteInvoke;
import com.xxy.user.bean.User;
import com.xxy.user.remote.UserRemote;
import org.springframework.stereotype.Service;

@Service
public class BasicService {
    @RemoteInvoke
    private UserRemote userRemote;

    public void testUser(){
        User user = new User();
        user.setAge(28);
        user.setName("张三");
        Object response = userRemote.saveUser(user);
        System.out.println(JSONObject.toJSONString(response));
    }

}
