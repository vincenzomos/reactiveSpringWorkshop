package nl.sogeti.reactivespring.service;

import nl.sogeti.reactivespring.model.OHLCData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class BitcoinDataService {

    protected final Log logger = LogFactory.getLog(this.getClass());
    private Path filePath;
    private ConnectableFlux<OHLCData> connectableFlux;
    private Flux<OHLCData> hotBitcoinPriceFlux;

    @Autowired
    public BitcoinDataService(Path filePath) {
        this.filePath = filePath;
    }

    @PostConstruct
    private void startHotBitcoinDataStream() {

        logger.info("Starting the Hot Flux.");
        hotBitcoinPriceFlux = getBitcoinData().publish().autoConnect();
        connectableFlux = getBitcoinData().publish();
        connectableFlux.connect();
        hotBitcoinPriceFlux.subscribe();
    }
    /**
     *
     * @return All the Bitcoin pricedata, emitting a price every second.
     */
    public Flux<OHLCData> getBitcoinData() {
        try (Stream<String> bitcoinMinutePrices = Files.lines(filePath)) {
            List<OHLCData> items = bitcoinMinutePrices
                    .map(line -> OHLCData.fromCSVLine(line))
                    .filter(optional -> optional.isPresent())
                    .map(Optional::get)
                    .collect(Collectors.toList());
            return Flux.fromIterable(items)
                    .delayElements(Duration.ofSeconds(1))
                    .onBackpressureDrop();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Flux.error(new IOException("Something went wrong with reading the data"));
    }

    public Flux<OHLCData> getHotBitcoinData() {
        return Flux.from(hotBitcoinPriceFlux);
    }

    public Flux<OHLCData> getHotBitcoinDataFromConnectable() {
        return Flux.from(connectableFlux);
    }

}
