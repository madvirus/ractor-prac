package webclient;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.CountDownLatch;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;

public class WebClientTest {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private WireMockServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = new WireMockServer(9999);
        mockServer.start();
    }

    @AfterEach
    void tearDown() {
        mockServer.stop();
    }

    @Test
    void name() throws InterruptedException {
        mockServer.stubFor(get("/hello1")
                .willReturn(aResponse()
                        .withHeader("Content-Type", "text/plain")
                        .withBody("hello1")
                        .withFixedDelay(3000)
                        .withStatus(200)));
        mockServer.stubFor(get("/hello2")
                .willReturn(aResponse()
                        .withHeader("Content-Type", "text/plain")
                        .withBody("hello2")
                        .withFixedDelay(5000)
                        .withStatus(200)));
        mockServer.stubFor(get("/hello3")
                .willReturn(aResponse()
                        .withHeader("Content-Type", "text/plain")
                        .withBody("hello3")
                        .withFixedDelay(4000)
                        .withStatus(200)));
        mockServer.stubFor(get("/hello4")
                .willReturn(aResponse()
                        .withHeader("Content-Type", "text/plain")
                        .withBody("hello4")
                        .withFixedDelay(4000)
                        .withStatus(200)));

        Mono<String> hello1 = WebClient.create("http://localhost:9999/hello1")
                .get()
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(resp -> logger.info("hello1 next"));

        Mono<String> hello2 = WebClient.create("http://localhost:9999/hello2")
                .get()
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(resp -> logger.info("hello2 next"));

        Mono<String> hello3 = WebClient.create("http://localhost:9999/hello3")
                .get()
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(resp -> logger.info("hello3 next"));

        Mono<String> hello4 = WebClient.create("http://localhost:9999/hello4")
                .get()
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(resp -> logger.info("hello4 next"));

        CountDownLatch latch = new CountDownLatch(1);
        logger.info("start subscribe");
        Mono.zip(hello1, hello2, hello3, hello4).subscribe(tup -> {
            logger.info("receive: " + tup.getT1() + ", " + tup.getT2() + ", " + tup.getT3() + ", " + tup.getT4());
            latch.countDown();
        });

        latch.await();
        logger.info("done");
    }
}
