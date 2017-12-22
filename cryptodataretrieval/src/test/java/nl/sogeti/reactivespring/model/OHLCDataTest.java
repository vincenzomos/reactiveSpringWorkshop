package nl.sogeti.reactivespring.model;

import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;


public class OHLCDataTest {

    @Test
    public void fromCSVLineGetsTheWeightedPrice() {
        String line = "18-12-2017 00:05;18878.99;18909.56;18850.01;18899;1.23;23300.38;18884.65";
        Optional<OHLCData> result  = OHLCData.fromCSVLine(line);
        String csvLine = result.get().printCSVLine();
        assertThat(Double.valueOf("18884.65") , equalTo(result.get().getWeightedPrice()));
        assertThat("18-12-2017 00:05;18878.99;18909.56;18850.01;18899.0;18884.65" , equalTo(csvLine));
    }
}