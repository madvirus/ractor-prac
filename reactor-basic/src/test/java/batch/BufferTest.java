package batch;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collection;
import java.util.List;

import static util.Sleep.sleep;

public class BufferTest {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    void bufferByCount() {
        Flux<List<Integer>> bufferSeq = Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .buffer(4);

        bufferSeq.subscribe(list -> logger.info("window: {}", list));
    }

    @Test
    void bufferByTime() {
        Flux<List<Long>> windowSeq = Flux.interval(Duration.ofMillis(100))
                .buffer(Duration.ofMillis(500), Duration.ofMillis(400));

        windowSeq.subscribe(list -> logger.info("window: {}", list));

        sleep(5000);
    }

    @Test
    void bufferUntil() {
        Flux.just(1,1,2,3,3,4)
                .bufferUntil(x -> x % 2 == 0)
                .subscribe(lst -> logger.info("window: {}", lst));
    }

    @Test
    void bufferWhile() {
        Flux.just(1,1,2,4,3,3,4,6,8,9,10)
                .bufferWhile(x -> x % 2 == 0)
                .subscribe(lst -> logger.info("window: {}", lst));
    }
}
