package com.xxy.netty.client;


import com.xxy.netty.constant.Constant;
import com.xxy.netty.handler.SimpleClientHandler;
import com.xxy.netty.request.ClientRequest;
import com.xxy.netty.response.Response;
import com.xxy.netty.util.RpcDecoder;
import com.xxy.netty.util.RpcEncoder;
import com.xxy.netty.zk.ZookeeperFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.Watcher;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TcpClient {
    static final Bootstrap bootstrap = new Bootstrap();
    static  ChannelFuture channelFuture = null;
    static  Set<String> realServerPath = new HashSet<>();
    static {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        //childHandler针对服务端的workerHandler,而客户端不需要
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                //将请求进行自定义编码实现protostuff序列化
                pipeline.addLast(new RpcEncoder(ClientRequest.class));
                pipeline.addLast(new RpcDecoder(Response.class));
                pipeline.addLast(new SimpleClientHandler());

            }
        });
        CuratorFramework client = ZookeeperFactory.create();
        String host = "localhost";
        int port = 8899;
        try {
            List<String> serverPaths = client.getChildren().forPath(Constant.SERVER_PATH);
            //加上zk监听服务器的变化
            CuratorWatcher watcher = new ServerWatcher();
            for (String serverPath : serverPaths) {
                String[] str = serverPath.split("#");
               realServerPath.add(str[0] + "#" + str[1]);
                ChannelManager.clear();
                ChannelManager.add(channelFuture);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
       /* try {
            //channelFuture=bootstrap.connect("localhost",8899).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }


    static int i = 0;
    /**
     * 发送数据
     * 注意：
     * 1、每一个请求都是同一个连接，并发问题
     * 1.1、需要request有唯一请求ID以及请求内容以及唯一响应ID以及响应内容
     * @param request
     * @return
     */
    public static Response send(ClientRequest request){
        channelFuture = ChannelManager.get(i);
        channelFuture.channel().writeAndFlush(request);
        DefaultFuture df = new DefaultFuture(request);
        return  df.get(1000);
    }
}
