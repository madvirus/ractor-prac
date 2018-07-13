package transform;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

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
}
