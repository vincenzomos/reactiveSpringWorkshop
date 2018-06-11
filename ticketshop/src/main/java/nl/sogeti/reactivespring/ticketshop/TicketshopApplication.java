package nl.sogeti.reactivespring.ticketshop;

import nl.sogeti.reactivespring.model.TicketRequest;
import nl.sogeti.reactivespring.model.TicketTransaction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
@ComponentScan(basePackages = {"nl.sogeti.reactivespring", "nl.sogeti.reactivespring.ticketshop"})
public class TicketshopApplication {


    /**
     * This commandlinerunner initializes the mongodb collection for us. We are creating a capped collection
     * here. This gives us the opportunity to make tailable cursors :https://docs.mongodb.com/manual/core/tailable-cursors/
     * Basically with this you keep seeing all new entries in a database when these are added.
     *
     * @param mongo
     * @return
     */
    @Bean
    public CommandLineRunner initData(MongoOperations mongo) {
        return (String... args) -> {
            mongo.dropCollection(TicketTransaction.class);
            mongo.createCollection(TicketTransaction.class, CollectionOptions.empty().size(1000000).capped());
        };
    }

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

        public Mono<ServerResponse> buyTickets(@Validated ServerRequest request) {

            Flux<TicketRequest> ticketRequestStream = request.bodyToFlux(TicketRequest.class);

            return ServerResponse.created(UriComponentsBuilder.fromPath("tickettransactions/").build().toUri())
                    .contentType(MediaType.APPLICATION_STREAM_JSON)
                    .body(ticketService.buyTickets(ticketRequestStream), TicketTransaction.class);
        }

        public Mono<ServerResponse> showTickets(ServerRequest request) {

            return ServerResponse.ok()
                    .contentType(MediaType.TEXT_EVENT_STREAM)
                    .body(ticketService.showTickets(), TicketTransaction.class);
        }

        public Mono<ServerResponse> showTicketsStream(ServerRequest request) {
            return ServerResponse.ok()
                    .contentType(MediaType.TEXT_EVENT_STREAM)
                    .body(ticketService.showTicketsTransactionsStream(), TicketTransaction.class);
        }
    }

    @Bean
    RouterFunction<?> routes(RouteHandler handler) {
        return route(POST("/buyTickets").and(contentType(MediaType.APPLICATION_STREAM_JSON)), handler::buyTickets)
            .andRoute(GET("/showTickets"), handler::showTickets)
            .andRoute(GET("/showTicketsStream"), handler::showTicketsStream);
    }
}

interface TicketRepository extends ReactiveMongoRepository<TicketTransaction, String> {

    @Tailable
    Flux<TicketTransaction> findTicketTransactionsBy();
}

@Service
class TicketService {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    /**
     * Service to handle a stream of Tickets
     *
     * @param ticketRequestStream
     */
    public Flux<TicketTransaction> buyTickets(Flux<TicketRequest> ticketRequestStream) {
        return ticketRepository.insert(ticketRequestStream.map(ticketRequest -> createTicketTransactionNew(ticketRequest)));
    }

    /**
     * Show all the tickets in the repo
     * @return
     */
    public Flux<TicketTransaction> showTickets() {
        return ticketRepository.findAll();
    }

    /**
     * Show a stream of stored TicketTransactions in the database
     * @return
     */
    public Flux<TicketTransaction> showTicketsTransactionsStream() {
        return ticketRepository.findTicketTransactionsBy();
    }

    private Mono<TicketTransaction> createTicketTransaction(UUID id, TicketRequest ticketRequest) {
        TicketTransaction transaction =  new TicketTransaction(id.toString(), ticketRequest.getEvent(), ticketRequest.getAmount(),
                ticketRequest.getCustomerName(), LocalDateTime.now());
        return ticketRepository.save(transaction);
    }

    private TicketTransaction createTicketTransactionNew(TicketRequest ticketRequest) {
        UUID id = UUID.randomUUID();
        TicketTransaction transaction =  new TicketTransaction(id.toString(), ticketRequest.getEvent(), ticketRequest.getAmount(),
                ticketRequest.getCustomerName(), LocalDateTime.now());
        return transaction;
    }
}