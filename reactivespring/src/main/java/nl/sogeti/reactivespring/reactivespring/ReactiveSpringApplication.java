package nl.sogeti.reactivespring.reactivespring;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Stream;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
public class ReactiveSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveSpringApplication.class, args);
    }

}

@Configuration
class FunctionRouteConfiguration {

    @Component
    public static class RouteHandler {

        private FluxFlixService service;

        public RouteHandler(FluxFlixService service) {
            this.service = service;
        }

        public Mono<ServerResponse> all(ServerRequest request) {
            return ServerResponse.ok()
                    .body(service.findAllMovies(), Movie.class);
        }

        public Mono<ServerResponse> findById(ServerRequest request) {
            return ServerResponse.ok()
                    .body(service.findById(request.pathVariable("movieId")), Movie.class);
        }

        public Mono<ServerResponse> streams(ServerRequest request) {
            return ServerResponse.ok()
                    .contentType(MediaType.TEXT_EVENT_STREAM)
                    .body(service.getEvents(request.pathVariable("movieId")), MovieEvent.class);
        }
    }
    @Bean
    RouterFunction<?> routes(RouteHandler handler){
        return route(GET("/movies"), handler::all)
                .andRoute(GET("/movies/{movieId}"), handler::findById)
                .andRoute(GET("/movies/{movieId}/events"), handler::streams);
    }
}

@Component
class SampleMoviesCLR implements CommandLineRunner {

    private final MovieRepository movieRepository;

    SampleMoviesCLR(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Stream.of("Silence of the lambda's", "Back to the future", "Enter the Mono<Void>",
                "Amores Perros", "Aeon Flux", "Meet the Fluxors", "The Fluxinator", "Lord of the Flux",
                "The Fluxtastic 4", "Mr Fluxtastic", "12 Monos")
                .forEach(title -> movieRepository.insert(new Movie(UUID.randomUUID().toString(), title))
                        .subscribe());

        movieRepository.findAll().subscribe(System.out::println);
    }
}

@Service
class FluxFlixService {
    private final MovieRepository movieRepository;

    public FluxFlixService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    Mono<Movie> findById(String id) {
        return this.movieRepository.findById(id);
    }

    Flux<Movie> findAllMovies() {
        return this.movieRepository.findAll();
    }

    Flux<MovieEvent> getEvents(String id) {
        return findById(id).flatMapMany(movie -> {
            Flux<MovieEvent> eventFlux = Flux.fromStream(Stream.generate(() -> new MovieEvent(id, new Date())));
            return eventFlux.zipWith(Flux.interval(Duration.ofSeconds(1L))).map(Tuple2::getT1);
        });
    }
}
interface MovieRepository extends ReactiveMongoRepository<Movie, String> {
}

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
class Movie {
    @Id
    private String id;
    private String title;
}

@Data
@AllArgsConstructor
class MovieEvent {
    private String id;
    private Date date;
}


