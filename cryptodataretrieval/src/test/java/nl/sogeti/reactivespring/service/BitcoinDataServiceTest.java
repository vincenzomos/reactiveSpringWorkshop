package nl.sogeti.reactivespring.service;

import nl.sogeti.reactivespring.model.OHLCData;
import nl.sogeti.reactivespring.util.Utils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reactivestreams.Publisher;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.Assert.*;

public class BitcoinDataServiceTest {

    private static List<String> datalines = null;
    private BitcoinDataService service;

    @BeforeClass
    public static void beforeClass() throws IOException, URISyntaxException {
        datalines = Utils.readDataFromResource("classpath:bitcoin_minuteData.csv");
    }

    @Before
    public void setUp() {
        service = new BitcoinDataService(datalines);
    }

    @Test
    public void getBitcoinData(){
        long expectedLineCount = datalines.size();

        StepVerifier.withVirtualTime(() -> service.getBitcoinData())
                .thenAwait(Duration.ofSeconds(expectedLineCount))
                .expectNextCount(expectedLineCount)
                .expectComplete()
                .log()
                .verify();

    }
}