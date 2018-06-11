package nl.sogeti.reactivespring.ticketshop;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.sogeti.reactivespring.model.TicketRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableWebFlux
public class TicketshopApplicationTest{

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    TicketRepository ticketRepository;

    @Test
    public void buyTicketTest() throws Exception {
        TicketRequest ticketRequest = new TicketRequest("Rolling Stones", "Kees", 5);
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writeValueAsString(ticketRequest));

        webTestClient.post().uri("/buyTickets")
                .contentType(MediaType.APPLICATION_STREAM_JSON)
//                .accept(MediaType.APPLICATION_JSON)
                .body(Flux.just(ticketRequest), TicketRequest.class)
                .exchange()
                .expectBody()
                .jsonPath("$.orderId").isNotEmpty()
                .jsonPath("$.event").isEqualTo("Rolling Stones");

    }

}