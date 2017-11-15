package reactor;

import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Created by Administrator on 2017/11/13.
 */
public class BufferTest {
    public static void main(String[] args) {
//        Flux.range(1,100).buffer(20).subscribe(System.out::println);
//        Flux.range(1,10).bufferUntil(i -> i % 2 == 0).subscribe(System.out::println);
//        Flux.range(1,10).bufferWhile(i -> i % 2 == 0).subscribe(System.out::println);
//        Flux.range(1,10).filter(i -> i % 2 == 0).subscribe(System.out::println);

//        Flux.range(1,100).window(20).subscribe(System.out::println);
//        Flux.interval(Duration.of(100, ChronoUnit.MILLIS)).window(1001).take(2).toStream().forEach(System.out::println);

        Flux.just("a","b").zipWith(Flux.just("c","d")).subscribe(System.out::println);
        Flux.just("a","b").zipWith(Flux.just("c","d"),(s1,s2)->String.format("%s-%s", s1, s2)).subscribe(System.out::println);

        Flux.range(1, 1000).take(10).subscribe(System.out::println);
        Flux.range(1, 1000).takeLast(10).subscribe(System.out::println);
        Flux.range(1, 1000).takeWhile(i -> i < 10).subscribe(System.out::println);
        Flux.range(1, 1000).takeUntil(i -> i == 10).subscribe(System.out::println);

        Flux.range(1,100).reduce((x,y)->x + y).subscribe(System.out::println);
        Flux.range(1,100).reduce(100,(x,y)->x + y).subscribe(System.out::println);


    }
}
