package com.shixin.test.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.ArrayList;
import java.util.List;


public class GuardedFuture {
    private  final static Logger log = LoggerFactory.getLogger(GuardedFuture.class);

    public static void main(String[] args) {
        GuardedObject object = new GuardedObject();
        new Thread(()->{
            // 子线程执行下载
            ArrayList response =new ArrayList();
            log.debug("download complete...");
            object.complete(response);
        }).start();

        log.debug("waiting...");
        // 主线程阻塞等待
        Object response = object.get();
        log.debug("get response: [{}] lines", ((List<String>) response).size());
    }
}

class GuardedObject{
    private Object response;
    private final Object lock = new Object();

    public Object get(){
        synchronized (lock)
        {
            while (response == null){
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return response;
        }
    }
    public void complete(Object o){
        synchronized (lock){
            this.response = o;
            lock.notifyAll();
        }
    }
}