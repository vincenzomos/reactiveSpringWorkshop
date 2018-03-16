package nl.sogeti.reactivespring.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class TicketRequest {

    private String event;
    private int amount;
    private String customerName;

    @JsonCreator
    public TicketRequest(@JsonProperty String event, @JsonProperty String customerName,  @JsonProperty int amount) {
        this.event = event;
        this.amount = amount;
        this.customerName = customerName;
    }
}
