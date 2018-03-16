package nl.sogeti.reactivespring.ticketshop;

import nl.sogeti.reactivespring.model.TicketRequest;
import nl.sogeti.reactivespring.model.TicketTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class TicketshopBuySimulator {

    private static Logger logger = LoggerFactory.getLogger(TicketshopBuySimulator.class);


    public static void main(String[] args) {

        WebClient client = WebClient.create("http://localhost:8086");

        TicketRequestFactory ticketRequestFactory = TicketRequestFactory.getInstance();
        Flux<TicketRequest> ticketStream = Flux.interval(Duration.ofSeconds(2)).map(i -> ticketRequestFactory.constructRandomTicketRequest());

        client.post()
                .uri("/buyTicket")
                .contentType(MediaType.APPLICATION_JSON)
//                .body(Mono.just(ticketRequestFactory.constructRandomTicketRequest()), TicketRequest.class)
                .body(ticketStream.take(2), TicketRequest.class)
                .retrieve()
                .bodyToFlux(TicketTransaction.class)
                .log()
                .doOnNext(ticketTransaction -> logger.info("bought: " + ticketTransaction))
                .blockLast();
//        client.post()
//                .uri("/buyTicket")
//                .contentType(MediaType.APPLICATION_STREAM_JSON)
//                .body(ticketStream.take(1), TicketRequest.class)
//                .retrieve()
//                .bodyToFlux(TicketTransaction.class)

//                .log()
//                .doOnNext(ticketTransaction -> logger.info("bought: " + ticketTransaction))
//                .blockLast();
//


    }

}
