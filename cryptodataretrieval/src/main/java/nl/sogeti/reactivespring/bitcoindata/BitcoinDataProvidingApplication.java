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
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * Example reactive application that will stream bitcoinprices
 *
 * Disclaimer: This class mixes the BaseApplication and the Routing of the endpoints. In a real world application
 * it is a better practice to have these in separated files.
 * But for this workshop this might be a better way to get an idea of
 * the integration of these parts.
 */
@SpringBootApplication
@ComponentScan(basePackages ={"nl.sogeti.reactivespring", "nl.sogeti.reactivespring.bitcoindata"})
public class BitcoinDataProvidingApplication {

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

            public Mono<ServerResponse> streamDataHotFlux(ServerRequest request) {
                return ServerResponse.ok()
                        .contentType(MediaType.TEXT_EVENT_STREAM)
                        .body(service.getHotBitcoinData(), OHLCData.class);
            }

            public Mono<ServerResponse> streamDataConnect(ServerRequest request) {
                return ServerResponse.ok()
                        .contentType(MediaType.TEXT_EVENT_STREAM)
                        .body(service.getHotBitcoinDataFromConnectable(), OHLCData.class);
            }

            public Mono<ServerResponse> streamSignals(ServerRequest request) {
                return ServerResponse.ok()
                        .contentType(MediaType.TEXT_EVENT_STREAM)
                        .body(tradingService.getTradingSignals(), Signal.class);
            }
        }
        @Bean
        RouterFunction<?> routes(RouteHandler handler){
            return route(GET("/streamData"), handler::streamData)
                    .andRoute(GET("/tradingAlerts"), handler::streamSignals)
                    .andRoute(GET("/streamDataHotFlux"), handler::streamDataHotFlux)
                    .andRoute(GET("/streamDataConnect"), handler::streamDataHotFlux);
        }

        @Bean
        ConnectableFlux<OHLCData> hotBitcoinData(BitcoinDataService service) {
            return service.getBitcoinData().publish();
        }
    }
}
