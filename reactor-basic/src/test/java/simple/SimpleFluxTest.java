package simple;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class SimpleFluxTest {
    @Test
    void just() {
        Flux<Integer> seq = Flux.just(1, 2, 3);
        seq.subscribe(value -> System.out.println("데이터 : " + value));
        StepVerifier.create(seq)
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .verifyComplete();
    }

    @Test
    void justEmptyFlux() {
        Flux<Integer> seq = Flux.just();
        StepVerifier.create(seq)
                .verifyComplete();
    }

    @Test
    void range() {
        Flux<Integer> seq = Flux.range(5, 3);
        StepVerifier.create(seq)
                .expectNext(5)
                .expectNext(6)
                .expectNext(7)
                .verifyComplete();
    }
}
