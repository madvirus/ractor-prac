package subscription;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
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

    @Test
    void subscriber() {
        Flux<Integer> seq = Flux.just(1, 2, 3);
        seq.subscribe(new Subscriber<>() {
            private Subscription subscription;
            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("Subscriber.onSubscribe");
                this.subscription = s;
                this.subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer i) {
                System.out.println("Subscriber.onNext: " + i);
                // this.subscription.request(1);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Subscriber.onError: " + t.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("Subscriber.onComplete");
            }
        });
    }
}
