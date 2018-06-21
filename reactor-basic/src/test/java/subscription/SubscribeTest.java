package subscription;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class SubscribeTest {
    @Test
    void subscribe() {
        Flux<Integer> seq = Flux.just(1, 2, 3)
                .doOnNext(i -> System.out.println("doOnNext: " + i));

        System.out.println("시퀀스 생성");
        seq.subscribe(i -> System.out.println("Received: " + i));
    }

    @Test
    void noSubscription() {
        Flux.just(1, 2, 3)
                .doOnNext(i -> System.out.println("doOnNext: " + i));
    }
}
