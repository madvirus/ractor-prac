package batch;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;

import static util.Sleep.sleep;

public class WindowTest {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    void windowByCount() {
        Flux<Flux<Integer>> windowSeq =
                Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                     .window(4);

        windowSeq.subscribe(seq -> {
            Mono<List<Integer>> monoList = seq.collectList();
            monoList.subscribe(list -> logger.info("window: {}", list));
        });

        Flux<Flux<Integer>> windowSeq2 =
                Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                        .window(4, 5); // 4개씩 새로운 Flux로 묶음

        windowSeq2.subscribe(seq -> { // seq는 Flux<Integer>
            Mono<List<Integer>> monoList = seq.collectList();
            monoList.subscribe(list -> logger.info("window: {}", list));
        });
    }

    @Test
    void windowByTime() {
        Flux<Flux<Long>> windowSeq = Flux.interval(Duration.ofMillis(100))
                .window(Duration.ofMillis(500), Duration.ofMillis(400));

        windowSeq.subscribe(seq -> {
            Mono<List<Long>> monoList = seq.collectList();
            monoList.subscribe(list -> logger.info("window: {}", list));
        });

        sleep(5000);
    }

    @Test
    void windowUntil() {
        Flux.just(1,1,2,3,3,4)
                .windowUntil(x -> x % 2 == 0)
                .subscribe(seq -> {
                    seq.collectList().subscribe(lst -> logger.info("window: {}", lst));
                });
    }

    @Test
    void windowWhile() {
        Flux.just(1,1,2,4,3,3,4,6,8,9,10)
                .windowWhile(x -> x % 2 == 0)
                .subscribe(seq -> {
                    seq.collectList().subscribe(lst -> logger.info("window: {}", lst));
                });
    }
}
