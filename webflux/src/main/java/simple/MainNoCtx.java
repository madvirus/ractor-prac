package simple;

import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.ipc.netty.NettyContext;
import reactor.ipc.netty.http.server.HttpServer;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

public class MainNoCtx {
    static RouterFunction<ServerResponse> routingFunction() {
        return route(path("/"), req -> ok().body(BodyInserters.fromObject("hello")));
    }

    public static void main(String[] args) {
        HttpHandler httpHandler = RouterFunctions.toHttpHandler(routingFunction());

        ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);
        NettyContext nettyContext = HttpServer.create("localhost", 8080)
                .newHandler(adapter)
                .block();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                nettyContext.dispose();
            }
        });
        nettyContext.onClose().block();
    }
}
