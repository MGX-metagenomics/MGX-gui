/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.pool;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sj
 */
public class MGXPool {

    private static ExecutorService pool;
    private static MGXPool instance;
    private static int numTasks = 0;

    private MGXPool() {
    }

    public synchronized static MGXPool getInstance() {
        if (instance == null) {
            int size = Math.min(20, Runtime.getRuntime().availableProcessors() + 3);
            pool = Executors.newFixedThreadPool(size);
            Logger.getLogger(MGXPool.class.getPackage().getName()).log(Level.INFO, "Started MGX processing pool with {0} slots.", size);
            instance = new MGXPool();
        }
        return instance;
    }

    static boolean isUpAndRunning() {
        return instance != null;
    }

    static int completedTaskNum() {
        return numTasks;
    }

    void shutdown() {
        pool.shutdown();
    }

    List<Runnable> shutdownNow() {
        return pool.shutdownNow();
    }

    public boolean isShutdown() {
        return pool.isShutdown();
    }

    public boolean isTerminated() {
        return pool.isTerminated();
    }

    boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return pool.awaitTermination(timeout, unit);
    }

    public <T> Future<T> submit(Callable<T> task) {
        numTasks++;
        return pool.submit(task);
    }

    public <T> Future<T> submit(Runnable task, T result) {
        numTasks++;
        return pool.submit(task, result);
    }

    public Future<?> submit(Runnable task) {
        numTasks++;
        return pool.submit(task);
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        numTasks += tasks.size();
        return pool.invokeAll(tasks);
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        numTasks += tasks.size();
        return pool.invokeAll(tasks, timeout, unit);
    }

    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        numTasks += tasks.size();
        return pool.invokeAny(tasks);
    }

    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        numTasks += tasks.size();
        return pool.invokeAny(tasks, timeout, unit);
    }

    public void execute(Runnable command) {
        numTasks++;
        pool.execute(command);
    }

}
