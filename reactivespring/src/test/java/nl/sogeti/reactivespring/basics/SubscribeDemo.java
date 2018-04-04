package nl.sogeti.reactivespring.basics;

import org.junit.Ignore;
import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

@Ignore
public class SubscribeDemo
{
    @Test
    public void demoGetDataFromAFlux() {
        List<Integer> elements = new ArrayList<>();

        Flux.just(1, 2, 3, 4)
                .doOnNext(elements::add)
                .log();

        assertThat(elements, contains(1, 2, 3, 4));
    }

    @Test
    public void demoSubcriberFullImpl() {
        List<Integer> elements = new ArrayList<>();

        Flux.just(1, 2, 3, 4)
                .log()
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        elements.add(integer);
                    }

                    @Override
                    public void onError(Throwable t) {}

                    @Override
                    public void onComplete() {}
                });

        assertThat(elements, contains(1, 2, 3, 4));
    }

    @Test
    public void demoSubcriberWithAdaptedBackpressure() {
        List<Integer> elements = new ArrayList<>();

        Flux.just(1, 2, 3, 4)
                .log()
                .subscribe(new Subscriber<Integer>() {

                    private Subscription s;
                    private int onNextAmount;

                    @Override
                    public void onSubscribe(Subscription s) {
                       this.s = s;
                       s.request(2);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        elements.add(integer);
                        onNextAmount++;
                        if (onNextAmount % 2 == 0) {
                            s.request(2);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {}

                    @Override
                    public void onComplete() {}
                });

        assertThat(elements, contains(1, 2, 3, 4));
    }
}
