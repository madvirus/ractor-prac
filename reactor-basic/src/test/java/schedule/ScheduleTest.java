package schedule;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.logging.Level;

public class ScheduleTest {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    void publishOn() {
        Flux.range(1, 10)
                .publishOn(Schedulers.newElastic("PUB P1"))
                .log(null, Level.FINE)
                .subscribe(new BaseSubscriber<Integer>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        logger.info("hookOnSubscribe"); // main thread
                        requestUnbounded();
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        logger.info("hookOnNext: " + value); // PUB P1 쓰레드
                    }

                    @Override
                    protected void hookOnComplete() {
                        logger.info("hookOnComplete"); // PUB P1 쓰레드
                    }
                });
    }

    @Test
    void subscribeOn() throws Exception {
        Flux.range(1, 10)
                .log()
                .subscribeOn(Schedulers.newParallel("SUB E1", 2))
                .subscribe(new BaseSubscriber<Integer>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        logger.info("hookOnSubscribe"); // main thread
                        requestUnbounded();
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        logger.info("hookOnNext: " + value); // SUB E1-1 쓰레드
                    }

                    @Override
                    protected void hookOnComplete() {
                        logger.info("hookOnComplete"); // SUB E1-1 쓰레드
                    }
                });

        Thread.sleep(1000);
    }

    @Test
    void pubSubOn() throws Exception {
        Flux.range(1, 6)
                .log()
                .map(i -> {
                    logger.info("map1: " + i + " --> " + (i + 20));
                    return i + 20;
                })
                .subscribeOn(Schedulers.newParallel("SUB", 2))
                .map(i -> {
                    logger.info("map2: " + i + " --> " + (i + 100));
                    return i + 100;
                })
                .publishOn(Schedulers.newParallel("PUB1", 2))
                .map(i -> {
                    logger.info("map3: " + i + " --> " + (i + 1000));
                    return i + 1000;
                })
                .publishOn(Schedulers.newParallel("PUB2", 2))
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
                    }
                });

        Thread.sleep(1000);
    }
}
