package com.schoolmarket.market_server.untils.tools.twoclass;

import com.schoolmarket.market_server.entiy.ClassUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class LoopTaskEx {

    private CompletableFuture<Void> loopFuture;

    private final classApi api;

    @Autowired
    public LoopTaskEx(classApi classApi) {
        this.api = classApi;
    }


    @Async
    public CompletableFuture<Void> startLoop(String acid,ClassUser user) throws IOException, ExecutionException, InterruptedException {
        loopFuture = new CompletableFuture<>();

        // 异步执行循环任务
        CompletableFuture.runAsync(Objects.requireNonNull(executeLoop(acid, user)));

        return loopFuture;
    }

    public void stopLoop() {
        if (loopFuture != null) {
            loopFuture.complete(null);
        }
    }

    private Runnable executeLoop(String acid, ClassUser user) throws IOException, ExecutionException, InterruptedException {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + 10000; // 10秒后结束循环

        while (!Thread.currentThread().isInterrupted() && System.currentTimeMillis() < endTime) {
            // 在这里编写你希望执行的循环任务逻辑
            // 这里是一个简单的示例，打印当前时间
            String response = api.signUptoActivity(acid,user).get();
            System.out.println(response);
            System.out.println("当前时间：" + System.currentTimeMillis());

            try {
                TimeUnit.MILLISECONDS.sleep(100); // 控制循环速度
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        loopFuture.complete(null); // 循环完成，通知任务完成
        return null;
    }
}
