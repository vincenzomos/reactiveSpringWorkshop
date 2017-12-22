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

    private final Double MINIMUM_MOVEMENT = 0.2;

    @Autowired
    private BitcoinDataService bitcoinPriceService;
    /**
     *
     * @return All the Bitcoin pricedata, emitting a price every second.
     */
    public Flux<Signal> getTradingSignals() {
        return bitcoinPriceService.getBitcoinData().buffer(5)
                .filter(list -> bigChange(list))
                .map(list -> createSignal(list));
    }

    private Signal createSignal(List<OHLCData> list) {
        OHLCData first = list.get(0);
        OHLCData last = list.get(4);
        Double difference = first.getWeightedPrice() - last.getWeightedPrice();
        return new Signal("Trading signal for BTC/USD", last.getDate(), Direction.getDirectionByValue(difference));
    }

    private boolean bigChange(List<OHLCData> list) {
        OHLCData first = list.get(0);
        OHLCData last = list.get(4);
        Double difference = Math.abs(first.getWeightedPrice() - last.getWeightedPrice());
        Double percentage = (difference/ first.getWeightedPrice()) * 100;
        return percentage > MINIMUM_MOVEMENT;
    }

    private Double calculatePercentage(Double difference, Double weightedPrice) {
        return (difference / weightedPrice) * 100.0;
    }
}