package nl.sogeti.reactivespring.bitcoindata;

import nl.sogeti.reactivespring.model.OHLCData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

/**
 * This class is responsible for testing the functional endpoint streamData
 */
@RunWith(SpringRunner.class)
//  We create a `@SpringBootTest`, starting an actual server on a `RANDOM_PORT`
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FunctionalStyleBitcoinDataConfigurationTest {

        // Spring Boot will create a `WebTestClient` for you,
        // already configure and ready to issue requests against "localhost:RANDOM_PORT"
        @Autowired
        private WebTestClient webTestClient;

        @Test
        public void streamDataTest() {
            List<OHLCData> result = webTestClient
                    // We then create a GET request to test an endpoint
                    .get().uri("/streamData")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    // and use the dedicated DSL to test assertions against the response
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.TEXT_EVENT_STREAM)
                    .returnResult(OHLCData.class)
                    .getResponseBody()
                    .take(3)
                    .collectList()
                    .block();

            assertThat(result, is(notNullValue()));
            assertEquals(result.size(), 3);
        }

}