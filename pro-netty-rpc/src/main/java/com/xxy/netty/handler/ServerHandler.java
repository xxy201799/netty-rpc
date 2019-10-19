package com.xxy.netty.handler;

import com.alibaba.fastjson.JSONObject;
import com.xxy.netty.constant.Constant;
import com.xxy.netty.medium.Media;
import com.xxy.netty.request.ClientRequest;
import com.xxy.netty.request.ServerRequest;
import com.xxy.netty.response.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class ServerHandler extends SimpleChannelInboundHandler<ClientRequest> {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        Response response = new Response();
        //如果是心跳检测事件
        if(evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            //假如是读空闲
            if(event.state().equals(IdleState.READER_IDLE)){
                System.out.println("读空闲====");
                //关掉channel
                ctx.channel().close();
            }else if(event.state().equals(IdleState.WRITER_IDLE)){//写空闲
                System.out.println("写空闲====");
            }else if(event.state().equals(IdleState.ALL_IDLE)){//读写空闲
                System.out.println("读写空闲");
                response.setMsg(Constant.IDLE_STATE_STR);
                ctx.channel().writeAndFlush(response);
            }
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ClientRequest msg) throws Exception {
        System.out.println("服务端接收到消息进行处理======");
        ServerRequest serverRequest = JSONObject.parseObject(JSONObject.toJSONString(msg),ServerRequest.class);

        Media media = Media.newInstance();
        Response response = media.process(serverRequest);
        System.out.println("消息处理完毕======");
        ctx.channel().writeAndFlush(response);
        System.out.println("消息写出完毕=======");
    }
}
