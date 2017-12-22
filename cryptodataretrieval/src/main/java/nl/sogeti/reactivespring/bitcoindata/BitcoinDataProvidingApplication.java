package nl.sogeti.reactivespring.bitcoindata;

import nl.sogeti.reactivespring.model.OHLCData;
import nl.sogeti.reactivespring.model.Signal;
import nl.sogeti.reactivespring.service.BitcoinDataService;
import nl.sogeti.reactivespring.service.TradingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
@ComponentScan(basePackages ={"nl.sogeti.reactivespring", "nl.sogeti.reactivespring.bitcoindata"})
public class BitcoinDataProvidingApplication {

    @Bean
    Writer fileWriter(){
        FileWriter writer = null;
        try {
            writer = new FileWriter(ClassLoader.getSystemResource("bitcoin_krakenData.csv").getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer;
    }

    @Bean
    WebClient client() {
        return WebClient.create("https://api.kraken.com/0/public");
    }

    @Bean
    Path bitcoinDataPath() {
        Path filePath = null;
        try {
            filePath =  Paths.get(ClassLoader.getSystemResource("bitcoin_minuteData.csv").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return filePath;
    }

    public static void main(String[] args) {
        SpringApplication.run(BitcoinDataProvidingApplication.class);
    }


    @Configuration
    class FunctionRouteConfiguration {
        @Component
        public class RouteHandler {

            @Autowired
            private BitcoinDataService service;

            @Autowired
            private TradingService tradingService;

            public RouteHandler(BitcoinDataService service) {
                this.service = service;
            }

            public Mono<ServerResponse> streamData(ServerRequest request) {
                return ServerResponse.ok()
                        .contentType(MediaType.TEXT_EVENT_STREAM)
                        .body(service.getBitcoinData(), OHLCData.class);
            }

            public Mono<ServerResponse> streamSignals(ServerRequest request) {
                return ServerResponse.ok()
                        .contentType(MediaType.TEXT_EVENT_STREAM)
                        .body(tradingService.getTradingSignals(), Signal.class);
            }

//            public Mono<ServerResponse> showProgressCollectingData(ServerRequest request) {
//                return ServerResponse.ok()
//                        .contentType(MediaType.TEXT_EVENT_STREAM)
//                        .body(service.startReadingData(), OHLCData.class);
//            }
        }
        @Bean
        RouterFunction<?> routes(RouteHandler handler){
            return route(GET("/streamData"), handler::streamData)
                    .andRoute(GET("/tradingAlerts"), handler::streamSignals);
        }
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
