package create;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import simple.SampleSubscriber;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CreatePushTest {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    void fluxCreate() {
        DataPump pump = new DataPump();

        Flux<Integer> bridge = Flux.create((FluxSink<Integer> sink) -> {
            pump.setListener(new DataListener<Integer>() {
                @Override
                public void onData(List<Integer> chunk) {
                    logger.info("onData: " + chunk);
                    chunk.forEach(s -> {
                        logger.info("sink.next: " + s);
                        sink.next(s); // push
                    });
                }

                @Override
                public void complete() {
                    logger.info("complete");
                    sink.complete();
                }
            });
            pump.start();
        }, FluxSink.OverflowStrategy.IGNORE);

        bridge.subscribe(new SampleSubscriber<>());

        sleep(3000L);

        pump.stop();
    }

    public interface DataListener<T> {
        void onData(List<T> chunk);

        void complete();
    }

    class DataPump {
        private AtomicBoolean isPumping = new AtomicBoolean(false);
        private int data = 1;
        private DataListener listener;
        private Thread thread;

        public DataPump() {
        }

        public void setListener(DataListener listener) {
            this.listener = listener;
        }

        public void start() {
            isPumping.set(true);
            thread = new Thread(() -> {
                while (isPumping.get()) {
                    if (listener != null) {
                        listener.onData(Arrays.asList(data++, data++, data++, data++, data++));
                    }
                    sleep(500L);
                }
            });
            thread.start();
        }

        public void stop() {
            isPumping.set(false);
            try {
                thread.join();
            } catch (InterruptedException e) {
            }
        }
    }

    private void sleep(long t) {
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
        }
    }
}
