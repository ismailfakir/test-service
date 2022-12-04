package net.cloudcentrik.autolink.testservice.task;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class TestRunnable implements Runnable {
    private final long countUntil;

    TestRunnable(long countUntil) {
        this.countUntil = countUntil;
    }

    @Override
    @SneakyThrows
    public void run() {
        long sum = 0;
        for (long i = 1; i < countUntil; i++) {
            log.info("...processing item: {}",i);
            sum += i;
            Thread.sleep(100);
        }
        log.info("Total Item processed: {}",sum);
    }
}