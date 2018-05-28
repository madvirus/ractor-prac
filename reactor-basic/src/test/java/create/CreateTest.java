package create;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import simple.SampleSubscriber;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CreateTest {
    @Test
    void fluxCreate() {
        DataPump pump = new DataPump();

        Flux<Integer> bridge = Flux.create((FluxSink<Integer> sink) -> {
            pump.setListener(new DataListener<Integer>() {
                @Override
                public void onData(List<Integer> chunk) {
                    System.out.println("onData: " + chunk);
                    chunk.forEach(s -> {
                        System.out.println("sink.next: " + s);
                        sink.next(s); // push
                    });
                }

                @Override
                public void complete() {
                    System.out.println("complete");
                    sink.complete();
                }
            });
            pump.start();
        }, FluxSink.OverflowStrategy.BUFFER);

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
