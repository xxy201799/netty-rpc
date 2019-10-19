package com.xxy.netty.init;

import com.xxy.netty.constant.Constant;
import com.xxy.netty.factory.ZookeeperFactory;
import com.xxy.netty.handler.ServerHandler;
import com.xxy.netty.request.ClientRequest;
import com.xxy.netty.response.Response;
import com.xxy.netty.util.RpcDecoder;
import com.xxy.netty.util.RpcEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

@Component
public class NettyInitial implements ApplicationListener<ContextRefreshedEvent> {
    public  void start() {
        //创建接收连接的线程池，默认创建线程数量cpu*2
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //创建处理任务的线程池
        EventLoopGroup workGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = null;
        try {
            bootstrap = new ServerBootstrap();
            //将两个线程池交给group进行处理
            bootstrap.group(bossGroup,workGroup);
            //不创建心跳包自定义实现心跳包
            bootstrap.option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new IdleStateHandler(60, 45, 20, TimeUnit.SECONDS));
                            //自定义解码器
                            pipeline.addLast(new RpcDecoder(ClientRequest.class));
                            pipeline.addLast(new RpcEncoder(Response.class));
                            //添加长连接心跳包
                            pipeline.addLast(new ServerHandler());
                        }
                    });
            int port = 8899;
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            CuratorFramework client = ZookeeperFactory.create();
            InetAddress inetAddress = InetAddress.getLocalHost();
            client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(Constant.SERVER_PATH + "/" + inetAddress.getHostAddress()+"#" + port + "#");
            channelFuture.channel().closeFuture().sync();
        }catch (Exception ex){
            ex.printStackTrace();
        }  finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        this.start();
    }
}
