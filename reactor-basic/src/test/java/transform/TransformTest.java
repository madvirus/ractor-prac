package transform;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

}
