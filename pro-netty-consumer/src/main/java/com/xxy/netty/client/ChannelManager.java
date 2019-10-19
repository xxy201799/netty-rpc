package com.xxy.netty.client;

import io.netty.channel.ChannelFuture;

import java.util.concurrent.CopyOnWriteArrayList;

public class ChannelManager {
    //创建一个可同时进行读写并线程安全的future链表
    public static CopyOnWriteArrayList<ChannelFuture> channelFutures = new CopyOnWriteArrayList<ChannelFuture>();

    public static void removeChannel(ChannelFuture channel){
        channelFutures.remove(channel);
    }
    public static void add(ChannelFuture channel){
        channelFutures.add(channel);
    }
    public static void clear(){
        channelFutures.clear();
    }


    public static ChannelFuture get(int i) {
        int size = channelFutures.size();
        ChannelFuture future = null;
        if(size > 0){
            future = channelFutures.get(0);
            i = 1;
        }else {
            future = channelFutures.get(i);
            i++;
        }
        return future;
    }
}
