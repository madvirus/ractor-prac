package schedule;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class TickTest {
    @Test
    void tick() throws InterruptedException {
        Flux.interval(Duration.ofSeconds(1))
                .subscribe(tick -> System.out.println("Tick " + tick));

        Thread.sleep(5000);
    }

    @Test
    void tickWithTimestamp() throws InterruptedException {
        Flux.interval(Duration.ofSeconds(1))
                .timestamp()
                .subscribe(tup -> System.out.println(tup))
        ;
        Thread.sleep(5000);
    }
}
