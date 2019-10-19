package com.xxy.netty.client;

import com.xxy.netty.constant.Constant;
import com.xxy.netty.zk.ZookeeperFactory;
import io.netty.channel.ChannelFuture;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.HashSet;
import java.util.List;

public class ServerWatcher implements CuratorWatcher {
    @Override
    public void process(WatchedEvent event) throws Exception {
        CuratorFramework client = ZookeeperFactory.create();
        String path = event.getPath();
        client.getChildren().usingWatcher(this).forPath(path);
        List<String> serverPaths = client.getChildren().forPath(path);


        for (String serverPath : serverPaths) {
            String[] str = serverPath.split("#");
            int weight = Integer.valueOf(str[2]);
            if(weight > 0){
                for(int w = 0; w <= weight; w++){
                    ChannelManager.realServerPath.add(str[0] + "#" + str[1]);
                }
            }
        }
        ChannelManager.clear();
        for(String realPath : ChannelManager.realServerPath){
            String[] str = realPath.split("#");
            try {
                int weight = Integer.valueOf(str[2]);
                if(weight > 0){
                    for(int w = 0; w <= weight; w++){
                        ChannelFuture channel = TcpClient.bootstrap.connect(str[0],Integer.valueOf(str[1]));
                        ChannelManager.add(channel);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
}
