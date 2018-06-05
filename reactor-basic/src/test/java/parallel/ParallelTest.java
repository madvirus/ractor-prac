package parallel;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class ParallelTest {
    @Test
    void parallel() throws Exception {
        Flux.range(1, 10)
                .parallel(2) // 작업을 레일로 나누기만 함
                .runOn(Schedulers.parallel())  // 각 레일을 병렬로 실행
                .subscribe(i -> System.out.println(Thread.currentThread().getName() + " -> " + i));

        Thread.sleep(1000);
    }
}
