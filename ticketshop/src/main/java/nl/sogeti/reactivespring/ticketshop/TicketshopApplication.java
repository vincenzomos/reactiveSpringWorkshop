package nl.sogeti.reactivespring.ticketshop;

import nl.sogeti.reactivespring.model.TicketRequest;
import nl.sogeti.reactivespring.model.TicketTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
@ComponentScan(basePackages = {"nl.sogeti.reactivespring", "nl.sogeti.reactivespring.ticketshop"})
public class TicketshopApplication {

//    @Bean
//    Path bitcoinDataPath() {
//        Path filePath = null;
//        try {
//            filePath = Paths.get(ClassLoader.getSystemResource("bitcoin_minuteData.csv").toURI());
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//        return filePath;
//    }

    public static void main(String[] args) {
        SpringApplication.run(TicketshopApplication.class);
    }

}

@Configuration
class FunctionRouteConfiguration {
    @Component
    public class RouteHandler {

        @Autowired
        private TicketService ticketService;

        public RouteHandler(TicketService ticketService) {
            this.ticketService = ticketService;
        }

        public Mono<ServerResponse> buyTicket(ServerRequest request) {

            Mono<TicketRequest> ticketRequest = request.bodyToMono(TicketRequest.class);
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ticketService.buyTicket(ticketRequest), TicketTransaction.class);
        }

        public Mono<ServerResponse> showTickets(ServerRequest request) {

            return ServerResponse.ok()
                    .contentType(MediaType.TEXT_EVENT_STREAM)
                    .body(ticketService.showTickets(), TicketTransaction.class);
        }
    }

    @Bean
    RouterFunction<?> routes(RouteHandler handler) {
        return route(POST("/buyTicket"), handler::buyTicket)
            .andRoute(GET("/showTickets"), handler::showTickets);
    }
}


interface TicketRepository extends ReactiveMongoRepository<TicketTransaction, String> {

}


@Service
class TicketService {

    private TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    /**
     * Service to buy a ticket
     *
     * @param ticketRequestMono
     */
    public Mono<TicketTransaction> buyTicket(Mono<TicketRequest> ticketRequestMono) {
        return ticketRequestMono.flatMap(request -> createTicketTransaction(request));
    }


    public Flux<TicketTransaction> showTickets() {
        return ticketRepository.findAll();
    }

    private Mono<TicketTransaction> createTicketTransaction(TicketRequest ticketRequest) {
        TicketTransaction transaction =  new TicketTransaction(UUID.randomUUID().toString(), ticketRequest.getEvent(), ticketRequest.getAmount(),
                ticketRequest.getCustomerName(), LocalDateTime.now());
        return ticketRepository.save(transaction);
    }
}