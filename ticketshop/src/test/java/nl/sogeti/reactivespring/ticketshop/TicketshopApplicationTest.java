package nl.sogeti.reactivespring.ticketshop;

import nl.sogeti.reactivespring.model.TicketRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TicketshopApplicationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    TicketRepository ticketRepository;

    @Test
    public void buyTicketTest() throws Exception {
        TicketRequest ticketRequest = new TicketRequest("Rolling Stones", 5, "Kees");

        webTestClient.post().uri("/buyTicket")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(ticketRequest), TicketRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.orderId").isNotEmpty()
                .jsonPath("$.event").isEqualTo("Rolling Stones");
    }

}