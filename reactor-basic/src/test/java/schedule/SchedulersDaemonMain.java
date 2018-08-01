package schedule;

import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.CountDownLatch;

public class SchedulersDaemonMain {

    public static void main(String[] args) throws InterruptedException {
        Logger logger = LoggerFactory.getLogger(SchedulersDaemonMain.class);
        CountDownLatch latch = new CountDownLatch(1);
        Scheduler schedulers1 = Schedulers.newElastic("SUB", 60, false);
        // Scheduler schedulers2 = Schedulers.elastic();

        Flux.range(1, 6)
                .log()
                .subscribeOn(schedulers1)
                .map(i -> {
                    logger.info("map: {} + 10", i);
                    return i + 10;
                })
                .subscribe(new BaseSubscriber<Integer>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        logger.info("hookOnSubscribe"); // main thread
                        request(1);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        logger.info("hookOnNext: " + value); // SUB 쓰레드
                        request(1);
                    }

                    @Override
                    protected void hookOnComplete() {
                        logger.info("hookOnComplete"); // SUB 쓰레드
                        latch.countDown();
                    }
                });

        latch.await();

        System.out.println("!!! BEFORE MAIN EXIT");
        showThreadNames();


        // schedulers1.dispose();

        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
            System.out.println("!!! AFTER MAIN EXIT");
            showThreadNames();

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }

            schedulers1.dispose();
            System.out.println("!!! AFTER DISPOSE");
            showThreadNames();
        });
        thread.start();
    }

    private static void showThreadNames() {
        Thread.getAllStackTraces().forEach((entry, trace) -> {
            System.out.println("thread name : " + entry.getName());
        });
    }
}
