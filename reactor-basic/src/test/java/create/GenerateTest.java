package create;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class GenerateTest {
    @Test
    void fluxGenerate() {
        Flux<String> flux = Flux.generate(
                () -> { // Callable<S> stateSupplier
                    System.out.println("init stateSupplier");
                    return 0;
                },
                (state, sink) -> { // BiFunction<S, SynchronousSink<T>, S> generator
                    System.out.println("sink.next");
                    sink.next("3 x " + state + " = " + 3 * state); // 한 번에 한 next()만 가능
                    if (state == 10) {
                        System.out.println("sink.complete");
                        sink.complete();
                    }
                    return state + 1;
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
