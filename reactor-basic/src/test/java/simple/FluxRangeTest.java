package simple;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class FluxRangeTest {

    @Test
    void range() {
        Flux<Integer> seq = Flux.range(11, 5);
        seq.subscribe(i -> System.out.println(i));
    }
}
