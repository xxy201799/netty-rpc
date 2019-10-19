package com.xxy.netty.service;

import com.xxy.netty.response.Response;
import com.xxy.user.bean.User;
import org.springframework.stereotype.Component;

@Component
public class UserService {
    public void saveUser(User user){
        System.out.println(user.toString());
    }
}
