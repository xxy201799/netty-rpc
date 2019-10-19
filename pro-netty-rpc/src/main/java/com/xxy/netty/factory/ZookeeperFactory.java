package com.xxy.netty.factory;

import com.xxy.netty.constant.Constant;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * 创建一个客户端工厂，建立连接
 */
public class ZookeeperFactory {
    public static CuratorFramework client = null;
    public static CuratorFramework create(){
        if(client == null){
            //重试机制，重试三次，每次隔一秒
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
            client = CuratorFrameworkFactory.newClient(Constant.ZK_ADDRESS,retryPolicy);
            client.start();
        }
        return client;
    }

    public static void main(String[] args) throws Exception{
        CuratorFramework client = create();
        client.create().forPath("/test");
    }


}
