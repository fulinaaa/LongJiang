package com.longjiang;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ThreadPoolTest {
    @Test
    public void test(){
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(5);
        Runnable t=new Runnable() {
            @Override
            public void run() {
                System.out.println("测试");
            }
        };
        pool.scheduleAtFixedRate(t,10000,1000, TimeUnit.MILLISECONDS);
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
