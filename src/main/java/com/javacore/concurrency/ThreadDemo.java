package com.javacore.concurrency;

public class ThreadDemo {

    public static void main(String[] args) throws InterruptedException {
        Runnable task = () -> {
            String threadName = Thread.currentThread().getName();
            System.out.println(threadName + " is running");
        };

        Thread thread = new Thread(task, "worker-thread");
        thread.start();
        thread.join();
    }
}
