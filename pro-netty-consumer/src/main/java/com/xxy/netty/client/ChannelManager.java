package com.xxy.netty.client;

import io.netty.channel.ChannelFuture;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ChannelManager {
    static AtomicInteger position = new AtomicInteger(0);
    static CopyOnWriteArrayList<String> realServerPath = new CopyOnWriteArrayList<String>();
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


    public static ChannelFuture get(AtomicInteger i) {
        int size = channelFutures.size();
        ChannelFuture future = null;
        if(i.get() > size){
            future = channelFutures.get(0);
            ChannelManager.position = new AtomicInteger(1);
        }else {
            future = channelFutures.get(i.getAndIncrement());
        }
        return future;
    }
}
