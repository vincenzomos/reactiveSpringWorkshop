package nl.sogeti.reactivespring.service;

import nl.sogeti.reactivespring.model.OHLCData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

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


    private Path filePath;

    @Autowired
    public BitcoinDataService(Path filePath) {
        this.filePath = filePath;
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
                    .delayElements(Duration.ofSeconds(1));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Flux.error(new IOException("Something went wrong with readingthe data"));
    }


//        /**
//         *
//         * @return
//         */
//        Flux<OHLCData> startReadingData() {
//                String response =
//                List<OHLCData> items = bitcoinMinutePrices
//                        .map(line -> createOHLCData(line))
//                        .collect(Collectors.toList());
//                return Flux.fromIterable(items)
//                        .delayElements(Duration.ofSeconds(1));
//
//
//            return Flux.error(new IOException("Something went wrong with readingthe data"));
//        }

    private void writeKrakenDataLine(OHLCData ohlcData) {

    }
}
