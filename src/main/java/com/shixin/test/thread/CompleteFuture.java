package com.shixin.test.thread;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.TestOnly;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.*;

import static java.lang.Thread.sleep;

@Slf4j
public class CompleteFuture {
    public static void main(String[] args) throws InterruptedException, ExecutionException {


        //+
        //
        //
       test1();

        // test2();

        //test3();

       // test4();
    }

    private static void test4() {
        CompletableFuture<String> cf
                = CompletableFuture.supplyAsync(() -> {
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "hello,楼下小黑哥";
        });// 1

        CompletableFuture<String> cf2 = cf.supplyAsync(() -> {
            try {
                sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "hello，程序通事";
        });
// 执行 OR 关系
        CompletableFuture<String> cf3 = cf2.applyToEither(cf, s -> s);

// 输出结果，由于 cf2 只休眠 3 秒，优先执行完毕
        System.out.println(cf3.join());
// 结果：hello，程序通事

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
            //这里需要注意一点，一旦 complete 设置成功，CompletableFuture 返回结果就不会被更改，
            // 即使后续 CompletableFuture 任务执行结束。
            //  cf.complete("ffff");

            cf.completeExceptionally(new RuntimeException("啊，挂了"));
        });

        log.debug("---------------{}", cf.get());

        sleep(1000);

        log.debug("---------------{}", cf.get());

    }

    //串行执行关系
    private static void test2() {


        CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> {
            return "test";
        });

        cf.thenApply(String::toLowerCase);

        //重新创建一个CompletionStage
        cf.thenCompose(s -> CompletableFuture.supplyAsync(s::toLowerCase));


        CompletableFuture<String> cf1
                = CompletableFuture.supplyAsync(() -> "hello,楼下小黑哥")// 1
                .thenApply(s -> s + "@程序通事") // 2
                .thenApply(String::toUpperCase); // 3
        System.out.println(cf1.join());


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



        Executors.newCachedThreadPool().execute(() -> {
            log.debug("等待结果线程。。。");
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //设置任务结果，由于cf任务未执行结束，结果返回true
            //这里需要注意一点，一旦 complete 设置成功，CompletableFuture 返回结果就不会被更改，
            // 即使后续 CompletableFuture 任务执行结束。
            //  cf.complete("ffff");

            //等待任务执行
            hireCar.join();
            log.debug("结果返回");
        });

        log.debug("执行完毕");
    }


}
