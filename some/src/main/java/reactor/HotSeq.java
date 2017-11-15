package reactor;

import reactor.core.publisher.Flux;

/**
 * Created by Administrator on 2017/11/14.
 */
public class HotSeq {
    public static void main(String[] args) throws InterruptedException {
        final Flux<Long> source = Flux.intervalMillis(1000)
                .take(10)
                .publish()
                .autoConnect();
        source.subscribe();
        Thread.sleep(5000);
        source
                .toStream()
                .forEach(System.out::println);
    }
}
