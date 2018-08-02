package parallel;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.concurrent.Queues;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static util.Sleep.sleep;

public class ParallelTest {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    void publishOnParallel() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Flux.range(1, 20)
                .publishOn(Schedulers.newParallel("PUB", 3))
                .map(x -> {
                    logger.info("MAP1 " + x);
                    sleep(100);
                    return x * 10;
                })
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Integer next) {
                        logger.info("onNext " + next);
                    }

                    @Override
                    public void onError(Throwable t) {
                        latch.countDown();
                    }

                    @Override
                    public void onComplete() {
                        latch.countDown();
                    }
                });
        latch.await();
    }

    @Test
    void parallel() throws Exception {
        int rails = 4;
        int countPerRail = 5;
        int totalCount = rails * countPerRail;
        CountDownLatch latch = new CountDownLatch(totalCount + 1);

        logger.info("QUEUE BUFFER SIZE: {}", Queues.SMALL_BUFFER_SIZE);
        Flux.range(1, totalCount)
                .log()
                .parallel(rails) // 작업을 레일로 나누기만 함
                .runOn(Schedulers.newParallel("PAR", 2), 2)  // 각 레일을 병렬로 실행
                .map(x -> {
                    sleep(50);
                    return String.format("%02d", x);
                })
                .subscribe(i -> {
                            logger.info("next {}", i);
                            latch.countDown();
                        },
                        err -> System.err.println(err),
                        () -> latch.countDown());

        latch.await();
    }

    private int nextSleepTime(int origin, int bound) {
        return ThreadLocalRandom.current().nextInt(origin, bound);
    }

    @Test
    void parallelAndRunOnSingle() throws Exception {
        CountDownLatch latch = new CountDownLatch(21);

        Flux.range(1, 20)
                .parallel(2) // 작업을 레일로 나누기만 함
                .runOn(Schedulers.newSingle("SINGLE"))
                .map(x -> {
                    int sleepTime = nextSleepTime(x % 2 == 0 ? 50 : 100, x % 2 == 0 ? 150 : 300);
                    logger.info("RAIL {}: {} sleepTime {}", x % 2, x, sleepTime);
                    sleep(sleepTime);
                    return String.format("%02d", x);
                })
                .subscribe(i -> {
                            logger.info("next {}", i);
                            latch.countDown();
                        },
                        err -> System.err.println(err),
                        () -> latch.countDown());

        latch.await(300 * 12, TimeUnit.MILLISECONDS);
    }

    @Test
    void parallelEndlessStream() {
        Flux.interval(Duration.ofMillis(10))
                .parallel(4)
                .runOn(Schedulers.newParallel("PAR", 2))
                .subscribe(x -> logger.info("{}", x));

        sleep(10000L);
    }

    @Test
    void multipleZip() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Mono m1 = Mono.just(1).map(x -> {
            logger.info("1 sleep");
            sleep(1500);
            return x;
        }).subscribeOn(Schedulers.parallel());

        Mono m2 = Mono.just(2).map(x -> {
            logger.info("2 sleep");
            sleep(3000);
            return x;
        }).subscribeOn(Schedulers.parallel());

        Mono m3 = Mono.just(3).map(x -> {
            logger.info("3 sleep");
            sleep(2000);
            return x;
        }).subscribeOn(Schedulers.parallel());

        logger.info("Mono.zip(m1, m2, m3)");
        Mono.zip(m1, m2, m3)
                //.log()
                .subscribe(
                        tup -> logger.info("next: {}", tup),
                        err -> {
                            System.err.println(err);
                            latch.countDown();
                            },
                        () -> latch.countDown());

        latch.await();
    }

    @Test
    void multipleZipDelayError() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Mono m1 = Mono.just(1).map(x -> {
            logger.info("1 sleep");
            sleep(2000);
            return x;
        }).subscribeOn(Schedulers.parallel());

        Mono m2 = Mono.just(2).map(x -> {
            logger.info("2 sleep");
            sleep(2000);
            return x;
        }).subscribeOn(Schedulers.parallel());

        Mono m3 = Mono.just(3).map(x -> {
            logger.info("3 sleep");
            sleep(1500);
            throw new RuntimeException("!");
        }).subscribeOn(Schedulers.parallel());

        Mono.zipDelayError(m1, m2, m3)
                .subscribe(
                        tup -> System.out.println(tup),
                        err -> {
                            System.err.println(err);
                            latch.countDown();
                            },
                        () -> latch.countDown());

        latch.await();
    }
}
