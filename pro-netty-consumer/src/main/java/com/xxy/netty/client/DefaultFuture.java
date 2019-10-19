package com.xxy.netty.client;

import com.xxy.netty.constant.Constant;
import com.xxy.netty.request.ClientRequest;
import com.xxy.netty.response.Response;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DefaultFuture  {
    //存储所有DefaultFuture
    public final static ConcurrentHashMap<Long,DefaultFuture> allDefaultFuture = new ConcurrentHashMap<Long, DefaultFuture>();
    final Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    private Response response;
    private long timeout = 2 * 60 * 1000;
    private long startTime = System.currentTimeMillis();

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public DefaultFuture(ClientRequest request) {
        //管理自身的Future
        allDefaultFuture.put(request.getId(), this);
    }
    //主线程获取数据，首先等待结果，
    public Response get(long timeout){
        lock.lock();
        try {
            while (!done()){
                condition.await(timeout, TimeUnit.SECONDS);
                if((System.currentTimeMillis() - startTime) > timeout){
                    System.out.println("请求超时====");
                    break;
                }
            }
        }catch (Exception e){

        }finally {
            lock.unlock();
        }
        return this.response;
    }
    public static void recevie(Response response){
        DefaultFuture defaultFuture = allDefaultFuture.get(response.getId());
        if(defaultFuture != null){
            Lock lock = defaultFuture.lock;
            lock.lock();
            try {
                defaultFuture.setResponse(response);
                defaultFuture.condition.signal();
                allDefaultFuture.remove(defaultFuture);
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                lock.unlock();
            }

        }
    }

    private boolean done() {
        if(this.response != null){
            return true;
        }
        return false;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    static class  FutureThread extends Thread{
        @Override
        public void run() {
            Set<Long> ids  = allDefaultFuture.keySet();
            for (Long id: ids ) {
                DefaultFuture df = allDefaultFuture.get(id);
                if(df == null){
                    allDefaultFuture.remove(df);
                }else {
                    //假如链路超时
                    if(df.getTimeout() < (System.currentTimeMillis() - df.getStartTime())){
                        Response response = new Response();
                        response.setId(id);
                        response.setCode(Constant.REQUEST_FAIL);
                        response.setMsg("链路请求超时");
                        recevie(response);
                    }
                }
            }

        }
    }
    static {
        FutureThread futureThread = new FutureThread();
        futureThread.setDaemon(true);
        futureThread.start();
    }
}
