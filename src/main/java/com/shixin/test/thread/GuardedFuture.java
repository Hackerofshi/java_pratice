package com.shixin.test.thread;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.*;

import static java.lang.Thread.sleep;

public class GuardedFuture {
    private final static Logger log = LoggerFactory.getLogger(GuardedFuture.class);

    public static void main(String[] args) throws InterruptedException {
        //test1();
        test2();


        List<GuardedObject> guardedObjects = new ArrayList<>();
        for (GuardedObject guardedObject : guardedObjects) {
            guardedObject.complete(new Object());
        }

    }

    private static void test2() throws InterruptedException {
        GuardedObjectV2 v2 = new GuardedObjectV2();
        new Thread(() -> {
            try {
                sleep(1000);
                v2.complete(null);
                sleep(4000);
                v2.complete(Arrays.asList("a", "b", "c"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        Object response = v2.get(2500);

        // 等待时间不足，就不会有结果返回
        // List<String> lines = v2.get(1500)


        if (response != null) {
            log.debug("get response: [{}] lines", ((List<String>) response).size());
        } else {
            log.debug("can't get response");
        }
    }

    private static void test1() {
        GuardedObject object = new GuardedObject();
        new Thread(() -> {
            // 子线程执行下载
            ArrayList response = download();
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("download complete...");
            object.complete(response);
        }).start();

        log.debug("waiting...");
        // 主线程阻塞等待
        Object response = object.get();
        log.debug("get response: [{}] lines", ((List<String>) response).size());
    }

    private static ArrayList download() {
        return new ArrayList();
    }
}

@Slf4j
class GuardedObject {
    private Object response;
    private final Object lock = new Object();

    public Object get() {
        synchronized (lock) {
            while (response == null) {
                try {
                    lock.wait(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return response;
        }
    }

    public void complete(Object o) {
        synchronized (lock) {
            this.response = o;
            lock.notifyAll();
        }
    }
}

@Slf4j
class GuardedObjectV2 {
    private Object response;
    private final Object lock = new Object();

    public Object get(long mills) throws InterruptedException {
        synchronized (lock) {
            //记录最初的时间
            long begin = System.currentTimeMillis();
            //已经经历的时间
            long timePassed = 0;
            while (response == null) {
                //假设millis 是1000，结果在400时唤醒了，那么还有600要等
                long waitTime = mills - timePassed;
                log.debug("waitTime: {}", waitTime);
                if (waitTime <= 0) {
                    log.debug("break...");
                    break;
                }
                try {
                    lock.wait(waitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sleep(2000);
                //如果提前被唤醒，这时已经经历的时间假设为400
                timePassed = System.currentTimeMillis() - begin;
                log.debug("timePassed: {}, object is null {}",
                        timePassed, response == null);
            }
            return response;
        }
    }

    public void complete(Object response) {
        synchronized (lock) {
            // 条件满足，通知等待线程
            this.response = response;
            log.debug("notify...");
            lock.notifyAll();
        }
    }
}

class GuardObjectV3 {
    private Object response;

    private int id;

    public GuardObjectV3(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    //获取结果，表示要等待多久
    public Object get(long timeout) {
        synchronized (this) {
            //开始时间
            long begin = System.currentTimeMillis();
            //经历时间
            long passTime = 0;
            while (response == null) {
                //每循环一次计算一次等待时间
                long waitTime = timeout - passTime;
                //经历时间超过了最大的等待时间，退出循环
                if (timeout - waitTime <= 0) {
                    break;
                }

                try {
                    this.wait(waitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                passTime = System.currentTimeMillis() - begin;
            }
            return  response;
        }
    }
    // 产生结果
    public void complete(Object response) {
        synchronized (this) {
            // 给结果成员变量赋值
            this.response = response;
            this.notifyAll();
        }
    }
}

class MailBoxes {
    private static Map<Integer,GuardObjectV3> boxes = new HashMap<>();

    private static int id = 1;

    //产生唯一的id
    private static synchronized int generateId(){
        return id++;
    }

    public static GuardObjectV3 getGuardedObject(int id){
        return boxes.remove(id);
    }
    public static GuardObjectV3 createGuardedObject() {
        GuardObjectV3 go = new GuardObjectV3(generateId());
        boxes.put(go.getId(), go);
        return go;
    }
    public static Set<Integer> getIds() {
        return boxes.keySet();
    }
}

@Slf4j
class People extends Thread{
    @Override
    public void run() {
        // 收信
        GuardObjectV3 guardedObject = MailBoxes.createGuardedObject();
        log.debug("开始收信 id:{}", guardedObject.getId());
        Object mail = guardedObject.get(5000);
        log.debug("收到信 id:{}, 内容:{}", guardedObject.getId(), mail);
    }
}

@Slf4j
class Postman extends Thread {
    private int id;
    private String mail;
    public Postman(int id, String mail) {
        this.id = id;
        this.mail = mail;
    }
    @Override
    public void run() {
        GuardObjectV3 guardedObject = MailBoxes.getGuardedObject(id);
        log.debug("送信 id:{}, 内容:{}", id, mail);
        guardedObject.complete(mail);
    }
}
