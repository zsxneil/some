package reactor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * Created by Administrator on 2017/11/13.
 */
public class MonoTest {
    public static void main(String[] args) {

        Hooks.onOperator(providedHook -> providedHook.operatorStacktrace());

        Mono.fromSupplier(()->"hello").subscribe(System.out::println);
        Mono.justOrEmpty(Optional.of("hello")).subscribe(System.out::println);
        Mono.create(monoSink -> monoSink.success("hello")).subscribe(System.out::println);

        Flux.range(1, 2).log("Range").subscribe(System.out::println);
        Flux.just(1, 0).map(x -> 1 / x).checkpoint("test").subscribe(System.out::println);

    }
}
