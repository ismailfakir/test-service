package net.cloudcentrik.autolink.testservice.task;

import lombok.extern.log4j.Log4j2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log4j2
public class TestTask {
    private static final int NTHREDS = 10;
    public static void startTestTask() {

        log.info("Starting long running job....");

        ExecutorService executor = Executors.newFixedThreadPool(NTHREDS);
        for (int i = 0; i < 10; i++) {
            Runnable worker = new TestRunnable(100L + i);
            executor.execute(worker);
        }
        // This will make the executor accept no new threads
        // and finish all existing threads in the queue
        executor.shutdown();
        // Wait until all threads are finish
        while (!executor.isTerminated()) {}
        log.info("Finished all jobs");

    }
}
