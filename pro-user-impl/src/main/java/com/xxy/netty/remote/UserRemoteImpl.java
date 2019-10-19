package com.xxy.netty.remote;

import com.xxy.netty.annotation.Remote;
import com.xxy.netty.response.Response;
import com.xxy.netty.service.UserService;
import com.xxy.netty.util.ResponseUtil;
import com.xxy.user.bean.User;
import com.xxy.user.remote.UserRemote;

import javax.annotation.Resource;

@Remote
public class UserRemoteImpl implements UserRemote {

    @Resource
    UserService userService;
    @Override
    public Object saveUser(User user) {
        userService.saveUser(user);
        return ResponseUtil.createSuccessResult(user);
    }
}
