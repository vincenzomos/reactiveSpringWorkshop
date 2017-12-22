package nl.sogeti.reactivespring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Class represent
 */
@Data
public class OHLCData {

    public static final DateTimeFormatter DATE_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern("dd-MM-yyyy H[H]:mm")
            .toFormatter();

    private String id;
    private LocalDateTime date;
    @JsonIgnore
    private Double high;
    @JsonIgnore
    private Double open;
    @JsonIgnore
    private Double low;
    @JsonIgnore
    private Double close;
    private Double weightedPrice;

    public OHLCData(String id, LocalDateTime date, Double open, Double high, Double low, Double close, Double weightedPrice) {
        this.id = id;
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.weightedPrice = weightedPrice;
    }

    public static Optional<OHLCData> fromCSVLine(String line) {
        String[] row = line.split(";");
        try {
            return Optional.of(new OHLCData(UUID.randomUUID().toString(), LocalDateTime.parse(row[0], OHLCData.DATE_FORMAT), Double.valueOf(row[1]), Double.valueOf(row[2]),
                    Double.valueOf(row[3]), Double.valueOf(row[4]), Double.valueOf(row[7])));
        }catch (Exception ex) {
            System.out.println("error when reading line : " + line);
            return Optional.empty();
        }
    }

    public String printCSVLine() {
        return getAllFieldsAsList().stream()
                .collect(Collectors.joining(";"));
    }

    private List<String> getAllFieldsAsList() {
        List<String> fields = new ArrayList<>();
        fields.add(date.format(DATE_FORMAT));
        fields.add(open.toString());
        fields.add(high.toString());
        fields.add(low.toString());
        fields.add(close.toString());
        fields.add(weightedPrice.toString());
        return fields;
    }


}
