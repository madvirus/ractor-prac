package create;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.Random;
import java.util.function.Consumer;

public class GenerateTest {
    private Logger logger = LoggerFactory.getLogger("GenerateTest");
    @Test
    void fluxGenerate() {
        Consumer<SynchronousSink<Integer>> randGen = new Consumer<>() {
            private int emitCount = 0;
            private Random rand = new Random();

            @Override
            public void accept(SynchronousSink<Integer> sink) {
                emitCount++;
                int data = rand.nextInt(100) + 1;
                logger.info("Generator sink next " + data);
                sink.next(data);
                if (emitCount == 10) {
                    logger.info("Generator sink complete");
                    sink.complete();
                }
            }
        };

        Flux<Integer> seq = Flux.generate(randGen);
        seq
                .subscribeOn(Schedulers.newElastic("SUB1"))
                .publishOn(Schedulers.newElastic("PUB1"))
                .subscribe(new BaseSubscriber<>() {
            private int receiveCount = 0;
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                logger.info("Subscriber#onSubscribe");
                logger.info("Subscriber request first 3 items");
                request(3);
            }

            @Override
            protected void hookOnNext(Integer value) {
                logger.info("Subscriber#onNext: " + value);
                receiveCount++;
                if (receiveCount % 3 == 0) {
                    logger.info("Subscriber request next 3 items");
                    request(3);
                }
            }

            @Override
            protected void hookOnComplete() {
                logger.info("Subscriber#onComplete");
            }
        });
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
        }
    }

    @Test
    void fluxGenerateWithState() {
        Flux<String> flux = Flux.generate(
                () -> { // Callable<S> stateSupplier
                    logger.info("init stateSupplier");
                    return 1;
                },
                (state, sink) -> { // BiFunction<S, SynchronousSink<T>, S> generator
                    logger.info("sink.next for " + state);
                    sink.next("3 x " + state + " = " + 3 * state); // 한 번에 한 next()만 가능
                    if (state == 10) {
                        logger.info("sink.complete");
                        sink.complete();
                    }
                    return state + 1;
                });

        flux.subscribe(new BaseSubscriber<>() {
            private int count = 0;

            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                logger.info("Subscriber.onSubscribe: request next 3 items");
                request(3);
            }

            @Override
            protected void hookOnNext(String value) {
                logger.info("Subscriber.onNext: " + value);
                count++;
                if (count % 3 == 0) {
                    System.out.println("Subscriber.onNext: request next 3 items");
                    request(3);
                }
            }

            @Override
            protected void hookOnComplete() {
                logger.info("Subscriber.onComplete");
            }
        });

        StepVerifier.create(flux)
                .expectNext("3 x 0 = 0")
                .expectNext("3 x 1 = 3")
                .expectNext("3 x 2 = 6")
                .expectNext("3 x 3 = 9")
                .expectNext("3 x 4 = 12")
                .expectNext("3 x 5 = 15")
                .expectNext("3 x 6 = 18")
                .expectNext("3 x 7 = 21")
                .expectNext("3 x 8 = 24")
                .expectNext("3 x 9 = 27")
                .expectNext("3 x 10 = 30")
                .verifyComplete();

    }
}
