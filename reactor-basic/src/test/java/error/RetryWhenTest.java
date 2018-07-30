package error;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class RetryWhenTest {
    @Test
    void retryWhen_3times_no_error() {
        Flux<Integer> seq = Flux.just(1, 2, 3)
                .map(i -> {
                    if (i < 3) return i;
                    else throw new IllegalStateException("force");
                })
                .retryWhen(errorsFlux -> errorsFlux.take(2));

        seq.subscribe(
                System.out::println,
                err -> System.err.println("에러 발생: " + err),
                () -> System.out.println("compelte")
        );
    }

    @Test
    void retryWhen_3times_error() {
        Flux<Integer> seq = Flux.just(1, 2, 3)
                .map(i -> {
                    if (i < 3) return i;
                    else throw new IllegalStateException("force");
                })
                .retryWhen(errorsFlux -> errorsFlux.zipWith(Flux.range(1, 3),
                        (error, index) -> {
                            if (index < 3) return index;
                            else throw new RuntimeException("companion error");
                        })
                );

        seq.subscribe(
                System.out::println,
                err -> System.err.println("에러 발생: " + err),
                () -> System.out.println("compelte")
        );
    }
}
