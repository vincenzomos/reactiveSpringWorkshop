package nl.sogeti.reactivespring.model;

/**
 *  Type of Signal
 */
public enum Direction {
    BEARISH, BULLISH, NO_MOVE;

    public static Direction getDirectionByValue(Double percentage) {
        if(percentage > 0) {
            return BULLISH;
        } else if (percentage < 0){
            return BEARISH;
        } else {
            return NO_MOVE;
        }
    }
}
