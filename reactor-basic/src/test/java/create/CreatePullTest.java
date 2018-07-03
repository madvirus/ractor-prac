package create;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;
import simple.SampleSubscriber;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static util.Sleep.sleep;

public class CreatePullTest {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    void pullModel() {
        Flux<Integer> flux = Flux.create((FluxSink<Integer> sink) -> {
            sink.onRequest(request -> {
                logger.info("++++ onRequest: {}, requestedFromDownstream: {}", request, sink.requestedFromDownstream());
                for (int i = 1; i <= request; i++) {
                    logger.info("++++ sink.next: {}", i);
                    sink.next(i);
                    logger.info("++++ requestedFromDownstream: {}", sink.requestedFromDownstream());
                }
            });
        });

        flux.log()
                .subscribeOn(Schedulers.newSingle("S1"))
                .publishOn(Schedulers.newSingle("P1"), 6)
                .subscribe(new BaseSubscriber<>() {
                    private int receiveCount = 0;

                    private void requestInternal(long req) {
                        logger.info("----> Subscriber.request: {}", req);
                        request(req);
                    }

                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        logger.info("----> Subscriber.onSubscribe");
                        requestInternal(3);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        logger.info("----> Subscriber.onNext: {}", value);
                        receiveCount++;
                        if (receiveCount == 3) {
                            logger.info("----> Subscriber.onNext: {}", value);
                            requestInternal(4);
                        }
                        if (receiveCount == 7) {
                            requestInternal(3);
                        }
                    }
                });
        sleep(3000L);
    }
}
