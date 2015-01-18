package ru.ifmo.md.extratask1.loading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by pinguinson on 16.01.2015.
 */
public class TimeoutTaskRunner implements Runnable {
    PhotoTask task;
    long timeout;

    private TimeoutTaskRunner(PhotoTask task, long timeout) {
        this.task = task;
        this.timeout = timeout;
    }

    public static void runTask(PhotoTask task, long timeout) {
        new Thread(new TimeoutTaskRunner(task, timeout)).start();
    }

    @Override
    public void run() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future future = executor.submit(task);

        try {
            future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        task.afterRun();
        executor.shutdown();
    }
}