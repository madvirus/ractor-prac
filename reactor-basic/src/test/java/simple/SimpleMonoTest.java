package simple;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class SimpleMonoTest {
    @Test
    void just() {
        Mono<Integer> seq = Mono.just(1);
        seq.subscribe(value -> System.out.println("데이터 : " + value));
        StepVerifier.create(seq)
                .expectNext(1)
                .verifyComplete();
    }

    @Test
    void justEmptyFlux() {
        Mono<Integer> seq = Mono.empty();
        StepVerifier.create(seq)
                .verifyComplete();
    }
}
