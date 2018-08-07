package logging;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

public class LoggingTest {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    void logging() {
        Flux.just(1, 2, 4, 5, 6)
                .log() // null, Level.INFO
                // .log("MYLOG", Level.FINE)
                .subscribe(x -> logger.info("next: {}", x));
    }
}
