package hotcold;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class HotFluxTest {
    @Test
    void hotFlux() {
        Flux<String> hot = createHotFlux();
    }

    private Flux<String> createHotFlux() {
        //Flux.create()
        return null;
    }
}
