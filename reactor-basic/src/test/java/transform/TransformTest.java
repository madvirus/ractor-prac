package transform;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

public class TransformTest {
    @Test
    void map() {
        Flux.just("a", "bc", "def", "wxyz")
                .map(str -> str.length())
                .subscribe(len -> System.out.println(len));
    }

    @Test
    void flatMap() {
        Flux.just(1, 2, 3)
                .flatMap(i -> Flux.range(1, i))
                .subscribe(System.out::println);
    }

    @Test
    void filter() {
        Flux.range(1, 10)
                .filter(num -> num % 2 == 0)
                .subscribe(x -> System.out.print(x + " -> "));
    }

    @Test
    void defaultIfEmpty() {
        Mono.empty()
                .defaultIfEmpty("empty1")
                .subscribe(x -> System.out.println(x));

        Flux.empty()
                .defaultIfEmpty("empty2")
                .subscribe(System.out::println);
    }

    @Test
    void startsWith() {
        Flux.just(1, 2, 3)
                .startWith(-1, 0)
                .subscribe(System.out::println);
    }

    @Test
    void concatWithValues() {
        Flux.just(1, 2, 3)
                .concatWithValues(100)
                .subscribe(System.out::println);
    }

    @Test
    void concatWith() throws InterruptedException {
        Flux<Integer> seq1 = Flux.just(1, 2, 3);
        Flux<Integer> seq2 = Flux.just(4, 5, 6);
        Flux<Integer> seq3 = Flux.just(7, 8, 9);

        seq1.concatWith(seq2).concatWith(seq3)
                .subscribe(System.out::println);
    }

    @Test
    void merge() throws InterruptedException {
        Flux<String> tick1 = Flux.interval(Duration.ofSeconds(1)).map(tick -> tick + "초틱");
        Flux<String> tick2 = Flux.interval(Duration.ofMillis(700)).map(tick -> tick + "밀리초틱");
        tick1.mergeWith(tick2).subscribe(System.out::println);

        Thread.sleep(5000);
    }

    @Test
    void zip() throws InterruptedException {
        Flux<String> tick1 = Flux.interval(Duration.ofSeconds(1)).map(tick -> tick + "초틱");
        Flux<String> tick2 = Flux.interval(Duration.ofMillis(700)).map(tick -> tick + "밀리초틱");
        tick1.zipWith(tick2).subscribe(tup -> System.out.println(tup));

        Thread.sleep(5000);
    }

    @Test
    void combineLatest() throws InterruptedException {
        Flux<String> tick1 = Flux.interval(Duration.ofSeconds(1)).map(tick -> tick + "초틱");
        Flux<String> tick2 = Flux.interval(Duration.ofMillis(700)).map(tick -> tick + "밀리초틱");
        Flux.combineLatest(tick1, tick2, (a, b) -> a + "\n" + b).subscribe(System.out::println);

        Thread.sleep(5000);
    }

    @Test
    void take() {
        CompleteWaitSubscriber sub = new CompleteWaitSubscriber();
        Flux.interval(Duration.ofSeconds(1))
                .take(5)
                .take(Duration.ofSeconds(10))
                .takeLast(4)
                .subscribe(sub);

        sub.waitComplete();
    }

    @Test
    void takeWhileUntil() {
        Flux.just(1, 2, 3, 4, 5)
                .takeWhile(x -> x < 4)
                .subscribe(System.out::println);

        Flux.just(1, 2, 3, 4, 5)
                .takeUntil(x -> x > 4)
                .subscribe(System.out::println);
    }

    @Test
    void skip() {
        CompleteWaitSubscriber sub = new CompleteWaitSubscriber();
        Flux.interval(Duration.ofMillis(500))
                .skipLast(3)
                .take(Duration.ofSeconds(5))
                .subscribe(sub);

        sub.waitComplete();
    }

    class CompleteWaitSubscriber extends BaseSubscriber {
        private CountDownLatch latch = new CountDownLatch(1);

        @Override
        protected void hookOnNext(Object value) {
            System.out.println(value);
        }

        @Override
        protected void hookFinally(SignalType type) {
            latch.countDown();
        }

        public void waitComplete() {
            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
