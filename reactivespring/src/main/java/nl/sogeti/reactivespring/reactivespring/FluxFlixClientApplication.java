package nl.sogeti.reactivespring.reactivespring;

import com.sun.jna.platform.unix.solaris.LibKstat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Date;

@SpringBootApplication
public class FluxFlixClientApplication {


    @Bean
    WebClient client() {
        return WebClient.create("http://localhost:8080/movies");

    }

    @Bean
    CommandLineRunner runner(WebClient client) {

        return  strings -> {
            client.get()
                    .uri("")
                    .exchange()
                    .flatMapMany(clientResponse -> clientResponse.bodyToFlux(Movie.class))
                    .filter(movie -> movie.getTitle().equalsIgnoreCase("12 monos"))
                    .subscribe(movie -> client.get()
                            .uri("/{id}/events", movie.getId())
                            .exchange()
                            .flatMapMany(clientResponse -> clientResponse.bodyToFlux(MovieEvent.class))
                            .subscribe(event -> System.out.println("PRINTING" + event)));
                    };
    }

    public static void main(String[] args) {
        SpringApplication.run(FluxFlixClientApplication.class);
    }
}
//
//@Data
//@AllArgsConstructor
//class Movie {
//    private String id, title;
//}
//
//@Data
//@AllArgsConstructor
//class MovieEvent {
//    private Movie movie;
//    private Date date;
//}
