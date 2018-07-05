package create;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class StreamTest {
    @Test
    void fromStream() throws IOException {
        Stream<String> straem = Files.lines(Paths.get("./pom.xml"));
        Flux<String> seq = Flux.fromStream(straem);
        seq.subscribe(System.out::println);
    }
}
