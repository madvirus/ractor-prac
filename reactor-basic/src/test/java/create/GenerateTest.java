package create;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;
import reactor.test.StepVerifier;

public class GenerateTest {

    @Test
    void fluxGenerate() {
        Flux<String> seq = Flux.generate((SynchronousSink<String> sink) -> {

        });
    }

    @Test
    void fluxGenerateWithState() {
        Flux<String> flux = Flux.generate(
                () -> { // Callable<S> stateSupplier
                    System.out.println("init stateSupplier");
                    return 0;
                },
                (state, sink) -> { // BiFunction<S, SynchronousSink<T>, S> generator
                    System.out.println("sink.next for " + state);
                    sink.next("3 x " + state + " = " + 3 * state); // 한 번에 한 next()만 가능
                    if (state == 10) {
                        System.out.println("sink.complete");
                        sink.complete();
                    }
                    return state + 1;
                });

        flux.subscribe(new BaseSubscriber<>() {
            private int count = 0;

            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                System.out.println("Subscriber.onSubscribe: request next 3 items");
                request(3);
            }

            @Override
            protected void hookOnNext(String value) {
                System.out.println("Subscriber.onNext: " + value);
                count++;
                if (count % 3 == 0) {
                    System.out.println("Subscriber.onNext: request next 3 items");
                    request(3);
                }
            }

            @Override
            protected void hookOnComplete() {
                System.out.println("Subscriber.onComplete");
            }
        });

        StepVerifier.create(flux)
                .expectNext("3 x 0 = 0")
                .expectNext("3 x 1 = 3")
                .expectNext("3 x 2 = 6")
                .expectNext("3 x 3 = 9")
                .expectNext("3 x 4 = 12")
                .expectNext("3 x 5 = 15")
                .expectNext("3 x 6 = 18")
                .expectNext("3 x 7 = 21")
                .expectNext("3 x 8 = 24")
                .expectNext("3 x 9 = 27")
                .expectNext("3 x 10 = 30")
                .verifyComplete();

    }
}
