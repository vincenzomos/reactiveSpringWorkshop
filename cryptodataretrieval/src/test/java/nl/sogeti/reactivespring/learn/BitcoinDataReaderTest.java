package nl.sogeti.reactivespring.learn;

import nl.sogeti.reactivespring.model.OHLCData;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BitcoinDataReaderTest {

    @Test
    public void testDatareader() {
        Path filePath = null;
        try {
            filePath =  Paths.get(ClassLoader.getSystemResource("bitcoin_minuteData.csv").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
//.map(line -> createOHLCData(line))
        try (Stream<String> bitcoinMinutePrices = Files.lines(filePath)) {
           List<OHLCData> items = bitcoinMinutePrices
                   .map(line -> OHLCData.fromCSVLine(line))
                   .filter(optional -> optional.isPresent())
                   .map(Optional::get)
                   .collect(Collectors.toList());
              Flux.fromIterable(items)
                    .delayElements(Duration.ofSeconds(1))
                    .log().subscribe(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    }
