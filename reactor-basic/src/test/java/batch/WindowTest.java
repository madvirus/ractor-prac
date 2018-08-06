package batch;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class WindowTest {
    @Test
    void windowByCount() {
        Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .log()
                .window(4, 1)
                .subscribe((Flux<Integer> group) -> {

                });
    }

    @Test
    void buffer() {
        Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .buffer(4)
                .subscribe(System.out::println);
    }
}
