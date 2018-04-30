package nl.sogeti.reactivespring.service;

import nl.sogeti.reactivespring.model.Direction;
import nl.sogeti.reactivespring.model.OHLCData;
import nl.sogeti.reactivespring.model.Signal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class TradingService {

    private static final Double MINIMUM_PRICECHANGE_PERCENTAGE = 0.2;

    @Autowired
    private BitcoinDataService bitcoinPriceService;

    /**
     * Gives a stream of Trading Signals
     * @return trading Signals as a {@link Flux<Signal>}
     */
    public Flux<Signal> getTradingSignals() {
        return bitcoinPriceService.getBitcoinData().buffer(5)
                .filter(list -> didThePriceMoveBig(list))
                .map(list -> createSignal(list));
    }

    private Signal createSignal(List<OHLCData> list) {
        OHLCData first = list.get(0);
        OHLCData last = list.get(list.size()-1);
        Double difference = first.getWeightedPrice() - last.getWeightedPrice();
        return new Signal("Trading signal for BTC/USD", last.getDate(), Direction.getDirectionByValue(difference));
    }

    private boolean didThePriceMoveBig(List<OHLCData> list) {
        OHLCData first = list.get(0);
        OHLCData last = list.get(list.size() -1);
        Double difference = Math.abs(first.getWeightedPrice() - last.getWeightedPrice());
        Double percentage = (difference/ first.getWeightedPrice()) * 100;
        return percentage > MINIMUM_PRICECHANGE_PERCENTAGE;
    }

}