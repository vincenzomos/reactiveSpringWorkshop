package nl.sogeti.reactivespring.learn;

import org.junit.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import static java.time.Duration.ofSeconds;

public class BaeldungTutorial {

    @Test
    public void testHotFlux() {

        ConnectableFlux<Object> publish = Flux.create(fluxSink -> {
            while (true) {
                fluxSink.next(System.currentTimeMillis());
            }
        })
                .sample(ofSeconds(2))
                .publish();

        publish.subscribe(System.out::println);
        publish.subscribe(item -> System.out.println("stream 2: " + item));
        publish.connect();
    }
}
