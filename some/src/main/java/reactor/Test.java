package reactor;

import org.hamcrest.Condition;
import org.junit.rules.Verifier;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

import java.time.Duration;

/**
 * Created by Administrator on 2017/11/14.
 */
public class Test {

    public static void main(String[] args) {
        StepVerifier
                .create(Flux.just("a","b"))
                .expectNext("a")
                .expectNext("b")
                .verifyComplete();


        StepVerifier.withVirtualTime(() -> Flux.interval(Duration.ofHours(4), Duration.ofDays(1)).take(2))
                .expectSubscription()
                .expectNoEvent(Duration.ofHours(4))
                .expectNext(0L)
                .thenAwait(Duration.ofDays(1))
                .expectNext(1L)
                .verifyComplete();

        final TestPublisher<String> testPublisher = TestPublisher.create();
        testPublisher.next("a");
        testPublisher.next("b");
        testPublisher.complete();

        StepVerifier.create(testPublisher)
                .expectNext("a")
                .expectNext("b")
                .expectComplete();
    }

}
