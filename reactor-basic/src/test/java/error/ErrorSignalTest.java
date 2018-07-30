package error;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.Random;

public class ErrorSignalTest {
    @Test
    void errorCounsumer() {
        Flux.range(1, 10)
                .map(x -> {
                    if (x == 5) throw new RuntimeException("exception");
                    else return x;
                })
                .subscribe(
                        i -> System.out.println(i),
                        ex -> System.err.println(ex.getMessage()),
                        () -> System.out.println("complete")
                );
    }

    @Test
    void onErrorReturn() {
        Flux<Integer> seq = Flux.range(1, 10)
                .map(x -> {
                    if (x == 5) throw new RuntimeException("exception");
                    else return x;
                })
                .onErrorReturn(-1);

        seq.subscribe(System.out::println);
    }

    @Test
    void onErrorResume() {
        Random random = new Random();
        Flux<Integer> seq = Flux.range(1, 10)
                .map(x -> {
                    int rand = random.nextInt(8);
                    if (rand == 0) throw new IllegalArgumentException("illarg");
                    if (rand == 1) throw new IllegalStateException("illstate");
                    if (rand == 2) throw new RuntimeException("exception");
                    return x;
                })
                .onErrorResume(error -> {
                    if (error instanceof IllegalArgumentException) {
                        return Flux.just(21, 22);
                    }
                    if (error instanceof IllegalStateException) {
                        return Flux.just(31, 32);
                    }
                    return Flux.error(error);
                });

        seq.subscribe(System.out::println);
    }
}
