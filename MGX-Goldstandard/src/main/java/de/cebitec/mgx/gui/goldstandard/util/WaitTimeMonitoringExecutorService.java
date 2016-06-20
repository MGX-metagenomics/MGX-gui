package de.cebitec.mgx.gui.goldstandard.util;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author pblumenk
 */
public class WaitTimeMonitoringExecutorService implements ExecutorService{

    private final ExecutorService target;

    public WaitTimeMonitoringExecutorService(ExecutorService target) {
        this.target = target;
    }

    @Override
    public <T> Future<T> submit(final Callable<T> task) {
        final long startTime = System.currentTimeMillis();
        return target.submit(new Callable<T>() {
            @Override
            public T call() throws Exception {
                final long queueDuration = System.currentTimeMillis() - startTime;
                System.out.println(String.format("Task %s spent %dms in queue", task, queueDuration));
                return task.call();
            }
        });
    }

    @Override
    public <T> Future<T> submit(final Runnable task, final T result) {
        final long startTime = System.currentTimeMillis();
        return submit(new Callable<T>() {
            @Override
            public T call() throws Exception {                
                final long queueDuration = System.currentTimeMillis() - startTime;
                System.out.println(String.format("Task %s spent %dms in queue", task, queueDuration));
                task.run();
                return null;
            }
        });
    }

    @Override
    public Future<?> submit(final Runnable task) {
        final long startTime = System.currentTimeMillis();
        return submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                final long queueDuration = System.currentTimeMillis() - startTime;
                System.out.println(String.format("Task %s spent %dms in queue", task, queueDuration));
                task.run();
                return null;
            }
        });
    }

    @Override
    public void shutdown() {
        target.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return target.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return target.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return target.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return target.awaitTermination(timeout, unit);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return target.invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return target.invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return target.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return target.invokeAny(tasks, timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        target.execute(command);
    }
}
