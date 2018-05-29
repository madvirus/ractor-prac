package create;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class HandleTest {
    @Test
    void handleTest() {
        Flux<Integer> seq = Flux.range(11, 10);

        // handle = map + filter 가능
        Flux<String> sseq = seq.handle((i, sink) -> {
            if (i % 3 == 0)
                sink.next("3의 배수 : " + i);
        });
        sseq.subscribe(System.out::println);
    }
}
