package nl.sogeti.reactivespring.model;

/**
 *  direction of a Signal
 */
public enum Direction {
    BEARISH, BULLISH;

    public static Direction getDirectionByValue(Double percentage) {
        if(percentage > 0) {
            return BULLISH;
        } else {
            return BEARISH;
        }
    }
}
