package simple;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;
import reactor.ipc.netty.NettyContext;
import reactor.ipc.netty.http.server.HttpServer;

public class Main {
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class)) {
            HttpHandler handler = WebHttpHandlerBuilder.applicationContext(ctx).build();

            ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(handler);
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
}
