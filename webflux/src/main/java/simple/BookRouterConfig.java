package simple;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.Arrays;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class BookRouterConfig {
    @Bean
    public RouterFunction<ServerResponse> monoRouterFunction() {
        return route(GET("/api/books").and(accept(MediaType.APPLICATION_JSON)), request ->
                ServerResponse.ok().body(BodyInserters.fromObject(Arrays.asList(
                        new Book("1234", "t1234"),
                        new Book("5678", "t5678")
                )))).andRoute(GET("/api/books/{id}").and(accept(MediaType.APPLICATION_JSON)), request -> {
                    String id = request.pathVariable("id");
                    return ServerResponse.ok().body(BodyInserters.fromObject(new Book(id, "t1234")));
                }
                );
    }

    public static class Book {
        private String isbn;
        private String title;

        public Book(String isbn, String title) {
            this.isbn = isbn;
            this.title = title;
        }

        public String getIsbn() {
            return isbn;
        }

        public String getTitle() {
            return title;
        }
    }
}
