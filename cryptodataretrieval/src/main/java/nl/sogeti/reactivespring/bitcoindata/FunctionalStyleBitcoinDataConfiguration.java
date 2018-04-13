package nl.sogeti.reactivespring.bitcoindata;

import nl.sogeti.reactivespring.model.OHLCData;
import nl.sogeti.reactivespring.model.Signal;
import nl.sogeti.reactivespring.service.BitcoinDataService;
import nl.sogeti.reactivespring.service.TradingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class FunctionalStyleBitcoinDataConfiguration {

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
