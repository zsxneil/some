package reactor;

import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Administrator on 2017/11/13.
 */
public class FluxTest {
    public static void main(String[] args) {
//        Flux.just("hello","world").subscribe(System.out::println);
//        Flux.fromArray(new Integer[]{1,3,5}).subscribe(System.out::println);
//        Flux.empty().subscribe(System.out::println);
//        Flux.range(1,10).subscribe(System.out::println);
        Flux.interval(Duration.of(10, ChronoUnit.SECONDS)).subscribe(System.out::println);

        Flux.generate(sink->{
            sink.next(1);
            sink.complete();
        }).subscribe(System.out::println);

        Random random = new Random();
        Flux.generate(ArrayList::new,(list,sink)->{
            int value = random.nextInt(10);
            list.add(value);
            sink.next(value);
            if (list.size() == 10)
                sink.complete();
            return list;
        }).subscribe(System.out::println);
        System.out.println("**********create************");
        Flux.create(fluxSink -> {
            for (Integer i = 0; i<5; i++) {
                fluxSink.next(i);
            }
            fluxSink.complete();
        }).subscribe(System.out::println);
    }
}
