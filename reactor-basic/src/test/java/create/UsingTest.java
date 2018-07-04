package create;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class UsingTest {
    @Test
    void using() {
        Flux<String> seq = Flux.using(() -> Files.lines(Paths.get("./pom.xml")),
                stream -> Flux.fromStream(stream),
                Stream::close);
        seq.subscribe(System.out::println);
    }
}
