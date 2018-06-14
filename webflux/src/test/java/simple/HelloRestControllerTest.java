package simple;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;

public class HelloRestControllerTest {
    @Test
    void get() {
        WebTestClient.bindToController(new HelloRestController())
                .build()
                .get()
                .uri("/hello.json")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("world")
                ;

    }
}
