package hotcold;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class ColdFluxTest {
    @Test
    void cold() {
        Flux<Integer> seq = Flux.just(1, 2, 3);
        seq.subscribe(v -> System.out.println("구독1: " + v));
        seq.subscribe(v -> System.out.println("구독2: " + v));
    }
}
