package com.shixin.test.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.LockSupport;

import static java.lang.Thread.sleep;

public class ParkTest {
    private  final static Logger log = LoggerFactory.getLogger(ParkTest.class);

    public static void main(String[] args) throws InterruptedException {
       Thread t1 =  new Thread(()->{
            log.debug("start...");
            try {
                sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("park...");
            LockSupport.park();
            log.debug("resume...");
        },"t1");
       t1.start();
        sleep(2);
        log.debug("unpark...");
        LockSupport.unpark(t1);
    }

}
