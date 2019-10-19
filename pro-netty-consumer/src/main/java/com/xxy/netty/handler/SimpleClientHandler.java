package com.xxy.netty.handler;

import com.alibaba.fastjson.JSONObject;
import com.xxy.netty.client.DefaultFuture;
import com.xxy.netty.constant.Constant;
import com.xxy.netty.response.Response;
import io.netty.channel.*;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutorGroup;

public class SimpleClientHandler extends SimpleChannelInboundHandler<Response> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Response msg) throws Exception {
        //如果收到读写空闲返回的数据
        //同样返回一个相同数据给服务器
        System.out.println(msg.getMsg());
        if(Constant.IDLE_STATE_STR.equals(msg.getMsg())){
            msg.setMsg(Constant.IDLE_STATE_STR);
            ctx.channel().writeAndFlush(msg);
            return;
        }
        ctx.channel().attr(AttributeKey.valueOf("ssss")).set(msg);
        Response response = msg;
        DefaultFuture.recevie(response);
    }
}
