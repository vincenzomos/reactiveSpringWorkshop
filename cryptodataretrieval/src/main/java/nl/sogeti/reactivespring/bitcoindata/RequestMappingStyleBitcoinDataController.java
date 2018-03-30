package nl.sogeti.reactivespring.bitcoindata;

import nl.sogeti.reactivespring.model.OHLCData;
import nl.sogeti.reactivespring.service.BitcoinDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/annotationBitcoinPrices")
public class RequestMappingStyleBitcoinDataController {

    private  BitcoinDataService bitcoinDataService = null;

    @Autowired
    public RequestMappingStyleBitcoinDataController(BitcoinDataService bitcoinDataService) {
        this.bitcoinDataService = bitcoinDataService;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<OHLCData> retrievebitCoinPrices() {
       return bitcoinDataService.getBitcoinData();
    }

}
