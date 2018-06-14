package webclient;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class WebClientTest {
    @Test
    void name() {
        Mono<String> dauMain = WebClient.create("https://www.daum.net")
                .get()
                .retrieve()
                .bodyToMono(String.class);

        System.out.println(dauMain.block());
//        dauMain.subscribe(body -> {
//            System.out.println("body body");
//            System.out.println(body);
//        });

        System.out.println("!!!!!!");
    }
}
