package ru.ifmo.md.extratask1;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by anton on 17/01/15.
 */
public class TimeoutTaskRunner implements Runnable {
    Runnable task;
    long timeout;

    private TimeoutTaskRunner(Runnable task, long timeout) {
        this.task = task;
        this.timeout = timeout;
    }

    public static void runTask(Runnable task, long timeout) {
        new Thread(new TimeoutTaskRunner(task, timeout)).start();
    }

    @Override
    public void run() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future future = executor.submit(task);

        try {
            future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executor.shutdown();
    }
}