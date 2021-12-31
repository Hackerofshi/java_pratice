package com.shixin.test.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

@Slf4j
public class CompleteFuture {
    public static void main(String[] args) throws InterruptedException, ExecutionException {


       //+
        //
        //
        // test1();

       // test2();

        test3();
    }

    private static void test3() throws InterruptedException, ExecutionException {
        CompletableFuture cf = CompletableFuture.supplyAsync(() -> {
            log.debug("任务开始");
            try {
                sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("任务执行结束");
            return "test";
        });

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //设置任务结果，由于cf任务未执行结束，结果返回true
            cf.complete("ffff");
        });

        log.debug("---------------{}", cf.get());

        sleep(1000);

        log.debug("---------------{}", cf.get());

    }

    private static void test2() {


        CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> {
            return "test";
        });

        cf.thenApply(String::toLowerCase);

        //重新创建一个CompletionStage
        cf.thenCompose(s -> CompletableFuture.supplyAsync(s::toLowerCase));

    }

    private static void test1() {
        CompletableFuture<String> orderAirplane = CompletableFuture.supplyAsync(() -> {
            log.debug("查询航班");
            try {
                sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("订购航班");
            return "航班订购成功";
        });


        CompletableFuture<String> orderHotel = CompletableFuture.supplyAsync(() -> {
            log.debug("查询酒店");
            try {
                sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("订购酒店");
            return "酒店订购成功";
        });

        CompletableFuture<String> hireCar = orderHotel.thenCombine(orderAirplane, (air, hotel) -> {
            log.debug("根据航班加酒店订购租车服务-{}-{}", air, hotel);
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("租车成功");
            return "租车信息";
        });

        //等待任务执行
        hireCar.join();
    }


}
