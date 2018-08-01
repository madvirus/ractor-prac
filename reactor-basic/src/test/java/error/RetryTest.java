package error;

import org.junit.jupiter.api.Test;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;

public class RetryTest {
    @Test
    void retry() {
        Flux.range(1, 5)
                .map(input -> {
                    if (input < 4) return "num " + input;
                    throw new RuntimeException("boom");
                })
                .retry(1)
                .subscribe(System.out::println, System.err::println);
    }

    @Test
    void retryWhenWithComplete() {
        Flux.error(new RuntimeException("force"))
                .retryWhen(errorFlux -> errorFlux.take(3))
                .subscribe(System.out::println, System.err::println);
    }

    @Test
    void retryWhenWithError() {
        Flux.error(new RuntimeException("force"))
                .retryWhen(errorFlux ->
                        errorFlux.zipWith(Flux.range(1, 4),
                                (error, index) -> {
                                    if (index < 4) return index;
                                    else throw Exceptions.propagate(error);
                                })
                )
                .subscribe(System.out::println, System.err::println);
    }
}
