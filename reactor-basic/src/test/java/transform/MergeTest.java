package transform;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import java.time.Duration;

import static util.Sleep.sleep;

public class MergeTest {
    @Test
    void merge() {
        Flux<String> seq1 = Flux.interval(Duration.ofSeconds(2)).map(l -> "SEQ1 " + l);
        Flux<String> seq2 = Flux.interval(Duration.ofSeconds(3)).map(l -> "SEQ2 " + l);

        seq1.withLatestFrom(seq2, (a, b) -> Tuples.of(a, b))
                .subscribe(System.out::println);

        sleep(20000);
    }
}
