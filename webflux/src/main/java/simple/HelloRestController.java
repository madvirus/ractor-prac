package simple;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class HelloRestController {
    @GetMapping("/hello.json")
    public Mono<Greeting> hello() {
        return Mono.just(new Greeting("world"));
    }

    public static class Greeting {
        private String name;

        public Greeting(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
