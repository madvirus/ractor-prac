package aggregation;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AggregateTest {
    @Test
    void collectList() {
        Mono<List<Integer>> monoList = Flux.just(1, 2, 3, 4, 5)
                .collectList();
    }

    @Test
    void collectMap() {
        Mono<Map<Integer, Tuple2<Integer, String>>> numTupMapMono =
                Flux.just(Tuples.of(1, "일"), Tuples.of(2, "이"), Tuples.of(3, "삼"), Tuples.of(4, "사"))
                        .collectMap(x -> x.getT1());

        Mono<Map<Integer, String>> numLabelMapMono =
                Flux.just(Tuples.of(1, "일"), Tuples.of(2, "이"), Tuples.of(3, "삼"), Tuples.of(4, "사"))
                        .collectMap(x -> x.getT1(),
                                x -> x.getT2());
        System.out.println(numLabelMapMono.block());

        Mono<Map<Integer, String>> numLabelTreeMapMono =
                Flux.just(Tuples.of(1, "일"), Tuples.of(2, "이"), Tuples.of(3, "삼"), Tuples.of(4, "사"))
                        .collectMap(x -> x.getT1(),
                                x -> x.getT2(),
                                () -> new TreeMap<>());

        Flux.just(1, 2, 3, 4)
                .collectMap(x -> x % 2)
                .subscribe(map -> System.out.println(map)); // {0=4, 1=3}
    }

    @Test
    void collectMultiMap() {
        Mono<Map<Integer, Collection<Integer>>> oddEvenList =
                Flux.just(1, 2, 3, 4).collectMultimap(x -> x % 2);
        oddEvenList.subscribe(map -> System.out.println(map));
    }

    @Test
    void count() {
        Mono<Long> countMono = Flux.just(1, 2, 3, 4).count();
        System.out.println(countMono.block());
    }

    @Test
    void reduce() {
        Flux.<Integer>just()
                .reduce((acc, x) -> acc - x)
                .defaultIfEmpty(0)
                .subscribe(System.out::println);

        Flux.<Integer>just(1)
                .reduce((acc, x) -> acc - x)
                .subscribe(System.out::println);

        Mono<Integer> mulMono = Flux.just(1, 2, 3, 4).reduce((acc, ele) -> acc * ele);
        mulMono.subscribe(sum -> System.out.println("sum : " + sum));

        Mono<String> strMono = Flux.just(1, 2, 3, 4).reduce("", (str, ele) -> str + "-" + ele.toString());
        strMono.subscribe(System.out::println);
    }

    @Test
    void scan() {
        Flux<Integer> seq = Flux.just(1, 2, 3, 4).scan((acc, x) -> acc * x);
        seq.subscribe(System.out::println);

        System.out.println("---");

        Flux<Integer> seq2 = Flux.just(2, 3, 4).scan(1, (acc, x) -> acc * x);
        seq2.subscribe(System.out::println);
    }

    @Test
    void all() {
        Mono<Boolean> all = Flux.just(1, 2, 3, 4).all(x -> x > 2);
        all.subscribe(b -> System.out.println("all: " + b));
        Mono<Boolean> any = Flux.just(1, 2, 3, 4).any(x -> x > 2);
        any.subscribe(b -> System.out.println("any: " + b));
        Mono<Boolean> hasElements = Flux.just(1, 2, 3, 4).hasElements();
        hasElements.subscribe(b -> System.out.println("hasElements: " + b));
        Mono<Boolean> hasElement = Flux.just(1, 2, 3, 4).hasElement(3);
        hasElement.subscribe(b -> System.out.println("hasElement: " + b));
    }
}
