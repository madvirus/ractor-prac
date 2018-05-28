package simple;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class SubscribeTest {
    @Test
    void customSubscriber() {
        SampleSubscriber<Integer> ss = new SampleSubscriber<>();
        Flux<Integer> ints = Flux.range(1, 4);
        ints.subscribe(i -> System.out.println("Consume: " + i),
                error -> System.err.println("Error: " + error),
                () -> System.out.println("Done"),
                s -> {
                    System.out.println("Subscription Consume");
                    s.request(Long.MAX_VALUE);
                });

        ints.subscribe(ss);
    }

}
