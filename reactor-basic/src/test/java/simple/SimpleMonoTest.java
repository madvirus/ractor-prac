package simple;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;

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
    void justNull() {
        Mono<Integer> seq1 = Mono.just(null);
        seq1.subscribe(value -> System.out.println("데이터 : " + value));
        StepVerifier.create(seq1)
                .verifyComplete();
    }

    @Test
    void emptyMono() {
        Mono<Integer> seq = Mono.empty();
        StepVerifier.create(seq)
                .verifyComplete();
    }

    @Test
    void justOrEmptyMono() {
        Mono<Integer> seq1 = Mono.justOrEmpty(null);
        StepVerifier.create(seq1).verifyComplete();
        Mono<Integer> seq2 = Mono.justOrEmpty(1);
        StepVerifier.create(seq2).expectNext(1).verifyComplete();
        Mono<Integer> seq3 = Mono.justOrEmpty(Optional.empty());
        StepVerifier.create(seq3).verifyComplete();
        Mono<Integer> seq4 = Mono.justOrEmpty(Optional.of(1));
        StepVerifier.create(seq4).expectNext(1).verifyComplete();
    }

}
