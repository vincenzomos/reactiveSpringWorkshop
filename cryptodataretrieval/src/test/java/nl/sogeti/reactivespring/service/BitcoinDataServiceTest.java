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

    private static Path filePath = null;
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
        long lineCount = 0;
        try {
            lineCount = Files.lines(filePath).count();
        } catch (IOException e) {
            fail("Unexpected IOException, test failed");
        }
        StepVerifier.withVirtualTime(() -> service.getBitcoinData())
                .thenAwait(Duration.ofSeconds(lineCount))
                .expectNextCount(lineCount)
                .expectComplete()
                .log()
                .verify();

    }
}