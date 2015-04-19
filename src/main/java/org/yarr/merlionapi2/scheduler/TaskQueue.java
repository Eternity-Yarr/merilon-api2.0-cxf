package org.yarr.merlionapi2.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TaskQueue
{
    private final static Logger log = LoggerFactory.getLogger(TaskQueue.class);
    ScheduledExecutorService executorService = Executors
            .newSingleThreadScheduledExecutor();
    private Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();

    private TaskQueue() {
        executorService.scheduleAtFixedRate(
                () -> TaskQueue.i().tick(), 1, 3, TimeUnit.MINUTES);
        Runtime.getRuntime().addShutdownHook(
                new Thread(){
                    @Override
                    public void run()
                    {
                        log.info("Shutting down Task queue");
                        TaskQueue.i().shutdown();
                    }
                }
        );
        log.info("Task queue initialized");
    }

    private void tick()
    {
        log.debug("Waking up, there is {} tasks in queue", tasks.size());
        while(tasks.peek() != null)
            try
            {
                tasks.remove().run();
            } catch (Exception e) {
                log.error("Got an exception while processing queued task: ", e.getMessage());
                log.debug("Exception trace: ", e);
            }
    }

    public void submitTask(@NotNull Runnable task) {
        if (task != null)
            tasks.add(task);
        else
            log.warn("Tried to submit null task");
    }

    public void shutdown() {
        this.executorService.shutdown();
    }

    public static TaskQueue i() {
        return Lazy.service;
    }

    private static class Lazy {
        public static final TaskQueue service = new TaskQueue();
    }
}
