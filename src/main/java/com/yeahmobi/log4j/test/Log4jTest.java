package com.yeahmobi.log4j.test;

import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Log4jTest {
    private static final Logger LOGGER = Logger.getLogger(Log4jTest.class);

    public static void main(String[] args) throws ParseException {

        Options options = new Options();
        options.addOption("p", "parallel", true, "Parallelism count");
        options.addOption("n", "number", true, "Number of logs to write down");

        CommandLineParser parser = new DefaultParser();

        CommandLine commandLine = parser.parse(options, args);

        int concurrency;
        int total;

        concurrency = Integer.parseInt(commandLine.getOptionValue("p", "10"));
        total = Integer.parseInt(commandLine.getOptionValue("n", "1000000"));


        CountDownLatch countDownLatch = new CountDownLatch(concurrency);
        AtomicInteger counter = new AtomicInteger(total);

        ExecutorService threadPool = Executors.newFixedThreadPool(concurrency);

        for (int i = 0; i < concurrency; i++) {
            threadPool.submit(new Task(countDownLatch, LOGGER, counter));
        }

        threadPool.shutdown();
    }

    static class Task implements Runnable {

        private final CountDownLatch countDownLatch;
        private final Logger logger;
        private final AtomicInteger counter;

        Task(CountDownLatch countDownLatch, Logger logger, AtomicInteger counter) {
            this.countDownLatch = countDownLatch;
            this.logger = logger;
            this.counter = counter;
        }

        public void run() {
            try {
                countDownLatch.countDown();
                countDownLatch.await();
                while (counter.getAndDecrement() > 0) {
                    logger.info("The general contract of the method <code>run</code> is that it may take any action whatsoever.");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
