package schedule;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.CountDownLatch;

public class ScheduleTest {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    void mainThread() {
        Flux.range(1, 3)
                .map(i -> {
                    logger.info("map {} to {}", i, i + 2);
                    return i + 2;
                })
                .flatMap(i -> {
                    logger.info("flatMap {} to Flux.range({}, {})", i, 1, i);
                    return Flux.range(1, i);
                })
                .subscribe(i -> logger.info("next " + i));
    }

    @Test
    void publishOn() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Flux.range(1, 6)
                .log()
                //.log(null, Level.FINE)
                .map(i -> {
                    logger.info("map 1: {} + 10", i);
                    return i + 10;
                })
                .publishOn(Schedulers.newElastic("PUB1"), 2)
                .map(i -> {
                    logger.info("map 2: {} + 10", i);
                    return i + 10;
                })
                .publishOn(Schedulers.newElastic("PUB2"), 3)
                .subscribe(new BaseSubscriber<Integer>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        logger.info("hookOnSubscribe"); // main thread
                        requestUnbounded();
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        logger.info("hookOnNext: " + value); // PUB P1 쓰레드
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                        }
                    }

                    @Override
                    protected void hookOnComplete() {
                        logger.info("hookOnComplete"); // PUB P1 쓰레드
                        latch.countDown();
                    }
                });
        latch.await();
    }

    @Test
    void subscribeOn() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Flux.range(1, 6)
                .log()
                .subscribeOn(Schedulers.newElastic("SUB", 10, false))
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
    }

    @Test
    void pubSubOn() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Flux.range(1, 6)
                .log()
                .subscribeOn(Schedulers.newElastic("SUB"))
                .map(i -> {
                    logger.info("map1: " + i + " --> " + (i + 20));
                    return i + 20;
                })
                .map(i -> {
                    logger.info("mapBySub: " + i + " --> " + (i + 100));
                    return i + 100;
                })
                .publishOn(Schedulers.newElastic("PUB1"), 2)
                .map(i -> {
                    logger.info("mapByPub1: " + i + " --> " + (i + 1000));
                    return i + 1000;
                })
                .publishOn(Schedulers.newElastic("PUB2"), 2)
                .subscribe(new BaseSubscriber<Integer>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        logger.info("hookOnSubscribe"); // main thread
                        request(1);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        logger.info("hookOnNext: " + value); // PUB 쓰레드
                        request(1);
                    }

                    @Override
                    protected void hookOnComplete() {
                        logger.info("hookOnComplete"); // PUB 쓰레드
                        latch.countDown();
                    }
                });

        latch.await();
    }
}
