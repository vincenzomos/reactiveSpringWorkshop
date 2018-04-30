package nl.sogeti.reactivespring.bitcoindata;

import nl.sogeti.reactivespring.model.OHLCData;
import nl.sogeti.reactivespring.model.Signal;
import nl.sogeti.reactivespring.service.BitcoinDataService;
import nl.sogeti.reactivespring.service.TradingService;
import nl.sogeti.reactivespring.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URISyntaxException;
import java.util.List;

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
    List<String> bitcoinDataList() {
        System.out.println("TESTLOG : Start creating datalines ");
        List<String> dataLines = null;
        try {
            dataLines = Utils.readDataFromResource("classpath:bitcoin_minuteData.csv");
            if (dataLines != null) {
                System.out.println("TESTLOG: datalines contains number :" + dataLines.size());
            }

        } catch (IOException e) {
            System.out.println("Something went wrong with readin the data");
            e.printStackTrace();
        }
        return dataLines;
    }


        public static void main(String[] args) {
        SpringApplication.run(BitcoinDataProvidingApplication.class);
    }
}
